package ee.ria.tara.mid.utils;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.commons.codec.digest.DigestUtils;

public final class Utils {
	private Utils() {
	}

	public static HttpURLConnection createConnection(URL url) throws Exception {
		if ("https".equalsIgnoreCase(url.getProtocol())) {
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			con.setHostnameVerifier((s, sslSession) -> {
				return true; // do not verify
			});
			SSLContext ctx = SSLContext.getInstance("TLSv1.2");
			ctx.init(
					null,
					new TrustManager[] { new TrustAllManager() },
					new SecureRandom()
			);
			con.setSSLSocketFactory(ctx.getSocketFactory());
			return con;
		} else {
			return (HttpURLConnection) url.openConnection();
		}
	}

	public static String calculateAtHash(String accessToken) {
		byte[] hashBytes = DigestUtils.sha256(accessToken.getBytes(UTF_8));
		byte[] leftHalf = Arrays.copyOf(hashBytes, 16);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(leftHalf);
	}
}
