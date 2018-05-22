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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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
import ee.ria.tara.mid.controller.response.TokenEndpointErrorResponse;
import ee.ria.tara.mid.controller.response.TokenEndpointResponse;
import ee.ria.tara.mid.controller.response.TokenResponse;
import ee.ria.tara.mid.utils.Properties;
import ee.ria.tara.mid.utils.Utils;


@RestController
@RequestMapping("/oauth")
class DemoRestController {

    private static final ObjectMapper JSON = new ObjectMapper();

    @Autowired
    private EndpointDiscovery endpointDiscovery;

    @RequestMapping(method = GET, value = "/request")
    void request(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String state = "abcdefghijklmnop";

        String scope = request.getParameter("scope");
        scope = scope != null ? "openid " + scope : "openid";

        String authorizationRequest = String.format("%s?scope=%s&response_type=%s&client_id=%s&redirect_uri=%s&state=%s"
                + "&nonce=%s&lang=%s", this.endpointDiscovery.getResponse().getAuthorizationEndpoint(),
            scope, "code", Properties.getApplicationId(), String.format("%s", Properties.getApplicationUrl()),
            state, "qrstuvwxyzabcdef", Properties.getApplicationLocale());
        Cookie cookie = new Cookie("TARAClient", state);
        response.addCookie(cookie);
        System.out.println(String.format("Authorization forwarded to <%s>", authorizationRequest));
        response.sendRedirect(authorizationRequest);
    }

    @RequestMapping(method = GET, value = "/response")
    void response(@RequestParam(required = false) String code,
                  @RequestParam(required = false) String error,
                  @RequestParam(name = "error_description", required = false) String errorDescription,
                  HttpServletResponse httpResponse,
                  HttpServletRequest httpRequest) throws Exception {
        if (error != null) {
            PrintWriter out = httpResponse.getWriter();
            out.println(String.format("RESPONSE ERROR: %s - %s", error, errorDescription));
            out.flush();
            return;
        }
        TokenResponse response = this.requestTokenEndpoint(code);
        if (!response.isInvalid()) {
            this.verifyTokens(response, httpRequest);
        }
        String json = JSON.writeValueAsString(response);
        httpResponse.setContentType("application/json");
        PrintWriter out = httpResponse.getWriter();
        out.print(json);
        out.flush();
    }

    private TokenResponse requestTokenEndpoint(String code) throws Exception {
        URL url = new URL(this.endpointDiscovery.getResponse().getTokenEndpoint());
        System.out.println(String.format("Requesting token from <%s>", url.toString()));
        HttpURLConnection connection = Utils.createConnection(url);
        connection.setRequestMethod(HttpMethod.POST.name());
        connection.setDoOutput(true);
        connection.setRequestProperty(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        connection.setRequestProperty(HttpHeaders.AUTHORIZATION, this.createHttpBasicAuthorizationHeader());
        try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
            String data = String.format("grant_type=authorization_code&code=%s&redirect_uri=%s", code,
                Properties.getApplicationUrl());
            System.out.println(String.format("Writing the following data <%s>", data));
            wr.writeBytes(data);
            wr.flush();
        }
        try {
            return JSON.readValue(connection.getInputStream(), TokenEndpointResponse.class);
        } catch (IOException e) {
            if (connection.getHeaderField("Content-Type").contains("application/json")) {
                return JSON.readValue(connection.getErrorStream(), TokenEndpointErrorResponse.class);
            }
            return new TokenEndpointErrorResponse(IOUtils.toString(connection.getErrorStream(), "UTF-8"));
        }
    }

    private void verifyTokens(TokenResponse response, HttpServletRequest httpRequest) throws ParseException,
        JOSEException {
        TokenEndpointResponse tokenResponse = (TokenEndpointResponse) response;
        SignedJWT jwt = SignedJWT.parse(tokenResponse.getIdToken());
        JWSVerifier verifier = new RSASSAVerifier(this.endpointDiscovery.getPublicKey());
        if (!jwt.verify(verifier)) {
            throw new RuntimeException("Invalid signature of ID Token");
        }
        JWTClaimsSet claimSet = jwt.getJWTClaimsSet();
        System.out.println("JWT (claims): " + claimSet);
        boolean cookieFound = false;
        for (Cookie cookie : httpRequest.getCookies()) {
            if (cookie.getName().equals("TARAClient")
                && cookie.getValue().equals(claimSet.getStringClaim("state"))) {
                cookieFound = true;
            }
        }
        if (!cookieFound) {
            throw new RuntimeException("Invalid state");
        }
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
        if (!Utils.calculateAtHash(tokenResponse.getAccessToken()).equals(atHash)) {
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
