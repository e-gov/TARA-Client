package ee.ria.tara.mid.controller.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EndpointDiscoveryResponse {
    private String issuer;

    @JsonProperty("authorization_endpoint")
    private String authorizationEndpoint;

    @JsonProperty("token_endpoint")
    private String tokenEndpoint;

    @JsonProperty("userinfo_endpoint")
    private String introspectionEndpoint;

    @JsonProperty("end_session_endpoint")
    private String endSessionEndpoint;

    @JsonProperty("id_token_signing_alg_values_supported")
    private List<String> supportedIdTokenSigningValues;

    @JsonProperty("jwks_uri")
    private String jwksUri;

    public String getIssuer() {
        return this.issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getAuthorizationEndpoint() {
        return this.authorizationEndpoint;
    }

    public void setAuthorizationEndpoint(String authorizationEndpoint) {
        this.authorizationEndpoint = authorizationEndpoint;
    }

    public String getTokenEndpoint() {
        return this.tokenEndpoint;
    }

    public void setTokenEndpoint(String tokenEndpoint) {
        this.tokenEndpoint = tokenEndpoint;
    }

    public String getIntrospectionEndpoint() {
        return this.introspectionEndpoint;
    }

    public void setIntrospectionEndpoint(String introspectionEndpoint) {
        this.introspectionEndpoint = introspectionEndpoint;
    }

    public List<String> getSupportedIdTokenSigningValues() {
        return this.supportedIdTokenSigningValues;
    }

    public void setSupportedIdTokenSigningValues(List<String> supportedIdTokenSigningValues) {
        this.supportedIdTokenSigningValues = supportedIdTokenSigningValues;
    }

    public String getJwksUri() {
        return this.jwksUri;
    }

    public void setJwksUri(String jwksUri) {
        this.jwksUri = jwksUri;
    }


    public String getEndSessionEndpoint() {
        return this.endSessionEndpoint;
    }

    public void setEndSessionEndpoint(String endSessionEndpoint) {
        this.endSessionEndpoint = endSessionEndpoint;
    }
}
