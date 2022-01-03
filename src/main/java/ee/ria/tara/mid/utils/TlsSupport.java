package ee.ria.tara.mid.utils;

import org.springframework.util.Assert;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.DatatypeConverter;
import java.io.InputStream;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Scanner;

public class TlsSupport {

    private static final String KEY = "/test-tls-key.pem";
    private static final String CERT = "/test-tls-cert.pem";

    private static final KeyFactory KEY_FACTORY;
    private static final CertificateFactory CERTIFICATE_FACTORY;
    static {
        try {
            KEY_FACTORY = KeyFactory.getInstance("RSA");
            CERTIFICATE_FACTORY = CertificateFactory.getInstance("X.509");
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static X509ExtendedKeyManager getKeyManager() throws Exception {
        try (InputStream k = TlsSupport.class.getResourceAsStream(KEY);
                InputStream c = TlsSupport.class.getResourceAsStream(CERT)) {
            Assert.notNull(k, "Key data stream is null");
            Assert.notNull(c, "Certificate data stream is null");
            return new TlsKeyManager(readPrivateKey(k), readCertificate(c));
        }
    }

    private static class TlsKeyManager extends X509ExtendedKeyManager {
        private static final String ALIAS = "IDTokenDemoApp";
        private final PrivateKey key;
        private final X509Certificate cert;

        TlsKeyManager(PrivateKey k, X509Certificate c) {
            super();
            this.key = k;
            this.cert = c;
        }

        @Override
        public String chooseEngineClientAlias(String[] strings,
                Principal[] principals, SSLEngine sslEngine) {
            return ALIAS;
        }

        @Override
        public String chooseEngineServerAlias(String s, Principal[] principals,
                SSLEngine sslEngine) {
            return ALIAS;
        }

        @Override
        public String[] getClientAliases(String s, Principal[] principals) {
            return new String[] { ALIAS };
        }

        @Override
        public String chooseClientAlias(String[] strings,
                Principal[] principals, Socket socket) {
            return ALIAS;
        }

        @Override
        public String[] getServerAliases(String s, Principal[] principals) {
            return new String[] { ALIAS };
        }

        @Override
        public String chooseServerAlias(String s, Principal[] principals,
                Socket socket) {
            return ALIAS;
       }

        @Override
        public X509Certificate[] getCertificateChain(String s) {
            return new X509Certificate[] { this.cert };
        }

        @Override
        public PrivateKey getPrivateKey(String s) {
            return this.key;
        }
    }

    public static TrustManager getTrustManager() {
        return new X509TrustManager() {

            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates,
                    String s) throws CertificateException {
                // trust
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates,
                    String s) throws CertificateException {
                // trust
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[] {};
            }
        };
    }

    private static PrivateKey readPrivateKey(InputStream is) throws Exception {
        return KEY_FACTORY.generatePrivate(
            new PKCS8EncodedKeySpec(
                DatatypeConverter.parseBase64Binary(
                    convertStreamToString(is).replaceAll("-----(.*)-----", "")
                )
            )
        );
    }

    private  static X509Certificate readCertificate(InputStream is)
            throws Exception {
        return (X509Certificate) CERTIFICATE_FACTORY.generateCertificate(is);
    }

    private static String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
