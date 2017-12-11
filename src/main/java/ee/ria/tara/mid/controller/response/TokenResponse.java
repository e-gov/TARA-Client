package ee.ria.tara.mid.controller.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by Janar Rahumeel (CGI Estonia)
 */

public interface TokenResponse {

    @JsonIgnore
    default boolean isInvalid() {
        return false;
    }

}
