package podcast.services;

import org.codehaus.jackson.JsonNode;
import org.springframework.stereotype.Service;
import podcast.utils.HTTP;

@Service
public class GoogleService {

  /**
   * Google authentication request according to
   * provided idToken retrieved on mobile client
   * @param idToken - String idToken
   * @return - JsonNode (response from Google)
   */
  public JsonNode googleAuthentication(String idToken) {
    String uri = "https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=" + idToken;
    return HTTP.get(uri);
  }

  /**
   * Google ID from response from Google authentication API
   * @param googleResp - JsonNode
   * @return - String (googleID)
   */
  public String googleIDFromResponse(JsonNode googleResp) {
    return googleResp.get("sub").asText();
  }

}
