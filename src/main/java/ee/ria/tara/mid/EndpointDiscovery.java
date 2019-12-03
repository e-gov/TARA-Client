package ee.ria.tara.mid;

import java.net.HttpURLConnection;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import ee.ria.tara.mid.utils.Properties;
import ee.ria.tara.mid.utils.Utils;
import ee.ria.tara.mid.controller.response.EndpointDiscoveryResponse;

public class EndpointDiscovery {

    final Logger logger = Logger.getLogger(EndpointDiscovery.class.getName());
    private static final ObjectMapper JSON = new ObjectMapper();

    private EndpointDiscoveryResponse response;
    private RSAPublicKey publicKey;

    public EndpointDiscovery() throws Exception {
        requestDiscoveryEndpoint();
        requestJwksEndpoint();
    }

    private void requestDiscoveryEndpoint() throws Exception {
        String discoveryEndpointUrl = String.format(
            "%s/.well-known/openid-configuration",
            Properties.getServiceProviderUrl()
        );
        logger.info(String.format("Requesting discovery endpoint <%s>", discoveryEndpointUrl));
        HttpURLConnection connection = Utils.createConnection(
            new URL(discoveryEndpointUrl)
        );
        connection.setRequestMethod(HttpMethod.GET.name());
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpStatus.OK.value()) {
            throw new RuntimeException(
                "Received unexpected HTTP response: " + responseCode
            );
        }
        this.response = JSON.readValue(
            connection.getInputStream(),
            EndpointDiscoveryResponse.class
        );
    }

    private void requestJwksEndpoint() throws Exception {
        logger.info(String.format("Requesting JWKS endpoint <%s>", this.response.getJwksUri()));
        HttpURLConnection connection = Utils.createConnection(
            new URL(this.response.getJwksUri())
        );
        connection.setRequestMethod(HttpMethod.GET.name());
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpStatus.OK.value()) {
            throw new RuntimeException(
                "Received unexpected HTTP response: " + responseCode
            );
        }

        JWKSet jwkSet = JWKSet.parse(
            IOUtils.toString(connection.getInputStream(), "UTF-8")
        );

        this.publicKey = ((RSAKey) jwkSet.getKeys().get(0)).toRSAPublicKey();
    }

    public EndpointDiscoveryResponse getResponse() {
        return this.response;
    }

    public RSAPublicKey getPublicKey() {
        return this.publicKey;
    }
}
