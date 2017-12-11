package ee.ria.tara.mid.controller.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by Janar Rahumeel (CGI Estonia)
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenEndpointErrorResponse implements TokenResponse {

    private String error;

    @JsonIgnore
    private String response;

    public TokenEndpointErrorResponse() {}

    public TokenEndpointErrorResponse(String response) {
        this.response = response;
    }

    @Override
    public boolean isInvalid() {
        return true;
    }

    /*
     * ACCESSORS
     */

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getResponse() {
        return response;
    }

}
