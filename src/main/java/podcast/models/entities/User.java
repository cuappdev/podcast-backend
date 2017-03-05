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
  @Getter private String firstName;
  @Getter private String lastName;
  @Getter private String imageURL;
  @Getter private String username;
  @Getter private Integer numberFollowers;
  @Getter private Integer numberFollowing;
  @Getter private Boolean isFollowing; // TODO
  @Getter private Session session;

  /**
   * Creation constructor
   * @param builder - UserBuilder
   */
  public User(UserBuilder builder) {
    // ID
    this.uuid = Generators.timeBasedGenerator().generate();

    // Google credentials
    this.googleID = builder.googleCreds.get("sub").asText();
    this.email = builder.googleCreds.get("email").asText().toLowerCase();
    this.firstName = builder.googleCreds.get("given_name") != null ?
      builder.googleCreds.get("given_name").asText() : null;
    this.lastName = builder.googleCreds.get("family_name") != null ?
      builder.googleCreds.get("family_name").asText() : null;
    this.imageURL = builder.googleCreds.get("picture").asText();

    // Other fields
    this.username = builder.username;
    this.session = builder.session;
    this.numberFollowers = 0; // FOR NOW
    this.numberFollowing = 0; // FOR NOW
    this.isFollowing = false; // FOR NOW
  }

  /**
   * See {@link Entity#toJsonObject()}
   */
  public JsonObject toJsonObject() {
    // TODO
    return null;
  }

  public static class UserBuilder {

    private JsonNode googleCreds;
    private String username;
    private Session session;

    /**
     * Constructor required fields
     * @param googleCreds - JsonNode
     */
    public UserBuilder(JsonNode googleCreds) {
      this.googleCreds = googleCreds;
    }

    /** Add username **/
    public UserBuilder username(String username) {
      this.username = username;
      return this;
    }

    /** Add session **/
    public UserBuilder session(Session session) {
      this.session = session;
      return this;
    }

  }

}
