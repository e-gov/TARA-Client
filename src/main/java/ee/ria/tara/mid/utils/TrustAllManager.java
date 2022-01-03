package ee.ria.tara.mid.utils;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

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
