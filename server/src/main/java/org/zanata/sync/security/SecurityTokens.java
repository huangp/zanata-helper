package org.zanata.sync.security;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.oltu.oauth2.common.token.OAuthToken;
import com.google.common.base.Throwables;

/**
 * @author Patrick Huang <a href="mailto:pahuang@redhat.com">pahuang@redhat.com</a>
 */
@SessionScoped
public class SecurityTokens implements Serializable {
    private String authorizationCode;

    public void setAuthorizationCode(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }

    public boolean hasAccess() {
        return authorizationCode != null;
    }

    public OAuthToken getOAuthToken() {
        if (!hasAccess()) {
            throw new IllegalStateException("You do not have authorization code");
        }
        try {
            OAuthClientRequest request = OAuthClientRequest
                    .tokenLocation("http://localhost:8080/zanata/rest/oauth/token")
                    .setGrantType(GrantType.AUTHORIZATION_CODE)
                    .setClientId("zanata_sync")
                    .setClientSecret("we_do_not_have_a_secret")
                    .setRedirectURI("http://www.example.com/redirect")
                    .setCode(authorizationCode)
                    .buildBodyMessage();

            //create OAuth client that uses custom http client under the hood
            OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());

            OAuthJSONAccessTokenResponse accessTokenResponse =
                    oAuthClient.accessToken(request, "POST");

            return accessTokenResponse.getOAuthToken();
        } catch (OAuthSystemException | OAuthProblemException e) {
            throw Throwables.propagate(e);
        }
    }
}
