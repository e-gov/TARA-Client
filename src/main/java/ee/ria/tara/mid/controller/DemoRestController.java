package ee.ria.tara.mid.controller;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Base64;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import ee.ria.tara.mid.EndpointDiscovery;
import ee.ria.tara.mid.controller.response.TokenEndpointResponse;
import ee.ria.tara.mid.utils.Properties;
import ee.ria.tara.mid.utils.Utils;

@RestController
@RequestMapping("/oauth")
class DemoRestController {
	private static final ObjectMapper JSON = new ObjectMapper();

	@Autowired
	private EndpointDiscovery endpointDiscovery;

	@RequestMapping(method = GET, value = "/request")
	void request(HttpServletResponse response) throws IOException {
		String authorizationRequest = String.format(
				"%s?scope=%s&response_type=%s&client_id=%s&redirect_uri=%s&state=%s"
						+ "&nonce=%s",
				this.endpointDiscovery.getResponse().getAuthorizationEndpoint(),
				"openid",
				"code",
				Properties.getApplicationId(),
				String.format("%s", Properties.getApplicationUrl()),
				"abcdefghijklmnop",
				"qrstuvwxyzabcdef"
		);

		response.sendRedirect(authorizationRequest);
	}

	@RequestMapping(method = GET, value = "/response")
	void response(@RequestParam(required = false) String code,
			@RequestParam(required = false) String error,
			HttpServletResponse httpResponse) throws Exception {
		if (error != null) {
			throw new RuntimeException("Authentication failed");
		}

		ObjectMapper mapper = new ObjectMapper();

		TokenEndpointResponse tokenResponse = requestTokenEndpoint(code);
		verifyTokens(tokenResponse);

		String tokenResponseString = mapper.writeValueAsString(tokenResponse);

		httpResponse.setContentType("text/html");
		PrintWriter out = httpResponse.getWriter();
		out.print("--------TokenResponse--------------");
		out.print("<br>");
		out.print(tokenResponseString);

		out.flush();

	}

	private TokenEndpointResponse requestTokenEndpoint(String code)
			throws Exception {
		URL url = new URL(
				this.endpointDiscovery.getResponse().getTokenEndpoint()
		);
		HttpURLConnection connection = Utils.createConnection(url);
		connection.setRequestMethod(HttpMethod.POST.name());
		connection.setDoOutput(true);
		connection.setRequestProperty(
				HttpHeaders.CONTENT_TYPE,
				MediaType.APPLICATION_FORM_URLENCODED_VALUE
		);
		connection.setRequestProperty(
				HttpHeaders.AUTHORIZATION, createHttpBasicAuthorizationHeader()
		);

		try (DataOutputStream wr =
				new DataOutputStream(connection.getOutputStream())) {
			wr.writeBytes(String.format(
					"grant_type=authorization_code&code=%s&redirect_uri=%s",
					code,
					String.format("%s", Properties.getApplicationUrl())
			));
			wr.flush();
		}
		int responseCode = connection.getResponseCode();
		if (responseCode != HttpStatus.OK.value()) {
			throw new RuntimeException(
					"Received unexpected HTTP response: " + responseCode
			);
		}

		return JSON.readValue(
				connection.getInputStream(), TokenEndpointResponse.class
		);
	}

	private void verifyTokens(TokenEndpointResponse tokenResponse)
			throws ParseException, JOSEException {
		SignedJWT signedJWT = SignedJWT.parse(tokenResponse.getIdToken());
		JWSVerifier verifier = new RSASSAVerifier(
				this.endpointDiscovery.getPublicKey()
		);
		if (!signedJWT.verify(verifier)) {
			throw new RuntimeException("Invalid signature of ID Token");
		}

		JWTClaimsSet claimSet = signedJWT.getJWTClaimsSet();
		if (!claimSet.getIssuer()
				.equals(this.endpointDiscovery.getResponse().getIssuer())) {
			throw new RuntimeException("Invalid issuer");
		}
		if (!claimSet.getAudience().contains(Properties.getApplicationId())) {
			throw new RuntimeException("Invalid audience");
		}
		if (claimSet.getExpirationTime().before(new Date())) {
			throw new RuntimeException("ID Token has expired");
		}

		String atHash = claimSet.getStringClaim("at_hash");
		if (!Utils.calculateAtHash(tokenResponse.getAccessToken())
				.equals(atHash)) {
			throw new RuntimeException("Invalid access token");
		}
	}

	private static String createHttpBasicAuthorizationHeader() {
		return String.format(
				"Basic %s",
				Base64.getEncoder().encodeToString(
						String.format(
								"%s:%s", Properties.getApplicationId(),
								Properties.getApplicationSecret()
						).getBytes(StandardCharsets.UTF_8)
				)
		);
	}
}
