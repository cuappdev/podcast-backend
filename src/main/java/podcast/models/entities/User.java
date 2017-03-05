package podcast.models.entities;

import com.couchbase.client.java.document.json.JsonObject;
import com.fasterxml.uuid.Generators;
import lombok.Getter;
import org.codehaus.jackson.JsonNode;
import java.util.UUID;

/**
 * App user (w/Google credentials)
 */
public class User extends Entity {

  @Getter private UUID uuid;
  @Getter private String googleID;
  @Getter private String email;
  @Getter private String name;
  @Getter private String imageURL;
  @Getter private String username;
  @Getter private Integer numberFollowers;
  @Getter private Integer numberFollowing;
  @Getter private Session session;

  /**
   * Creation constructor
   * @param googleCreds - JSON response from token req to Google
   * @param username - Username String
   * @param session - Session
   */
  public User(JsonNode googleCreds, String username, Session session) {
    // ID
    this.uuid = Generators.timeBasedGenerator().generate();

    // Google credentials
    this.googleID = googleCreds.get("sub").asText();
    this.email = googleCreds.get("email").asText().toLowerCase();
    this.name = googleCreds.get("given_name").asText().toLowerCase() +
      googleCreds.get("family_name").asText().toLowerCase();
    this.imageURL = googleCreds.get("picture").asText();

    // Other fields
    this.username = username;
    this.numberFollowers = 0; // FOR NOW
    this.numberFollowing = 0; // FOR NOW
    this.session = session;
  }

  /**
   * See {@link Entity#toJsonObject()}
   */
  public JsonObject toJsonObject() {
    // TODO
    return null;
  }

}
