package ee.ria.tara.mid.utils;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;

public class TrustAllManager implements X509TrustManager {
    @Override
    public void checkClientTrusted(X509Certificate[] c, String s)
        throws CertificateException {
        // trust all
    }

    @Override
    public void checkServerTrusted(X509Certificate[] c, String s)
        throws CertificateException {
        // trust all
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[] {};
    }
}
