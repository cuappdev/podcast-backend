package podcast.models.entities;

import com.couchbase.client.java.document.json.JsonObject;
import com.fasterxml.uuid.Generators;
import lombok.Getter;
import lombok.Setter;
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
  @Getter private Integer numberFollowers;
  @Getter private Integer numberFollowing;
  @Getter private Boolean isFollowing;
  @Getter private Session session;
  @Setter @Getter private String username;

  /**
   * Constructor from builder
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
    this.session = builder.session;
    this.numberFollowers = 0; // FOR NOW
    this.numberFollowing = 0; // FOR NOW
    this.isFollowing = false; // FOR NOW

    // Generate
    this.username = "user-" + this.uuid;
  }


  /**
   * Constructor from Couchbase JsonObject
   * @param object - JsonObject from Couchbase
   */
  public User(JsonObject object) {
    this.uuid = UUID.fromString(object.getString("uuid"));
    this.googleID = object.getString("googleID");
    this.email = object.getString("email");
    this.firstName = object.getString("firstName");
    this.lastName = object.getString("lastName");
    this.imageURL = object.getString("imageURL");
    this.session = new Session(object.getObject("session"));
    this.numberFollowers = object.getInt("numberFollowers");
    this.numberFollowing = object.getInt("numberFollowing");
    this.isFollowing = object.getBoolean("isFollowing");
    this.username = object.getString("username");
  }


  /**
   * See {@link Entity#toJsonObject()}
   */
  public JsonObject toJsonObject() {
    JsonObject result = JsonObject.create();
    result.put("uuid", uuid);
    result.put("googleID", googleID);
    result.put("email", email);
    result.put("firstName", firstName);
    result.put("lastName", lastName);
    result.put("imageURL", imageURL);
    result.put("username", username);
    result.put("session", session.toJsonObject());
    result.put("numberFollowers", numberFollowers);
    result.put("numberFollowing", numberFollowing);
    result.put("isFollowing", isFollowing);
    return result;
  }


  public static class UserBuilder {

    private JsonNode googleCreds;
    private Session session;

    /**
     * Constructor required fields
     * @param googleCreds - JsonNode
     */
    public UserBuilder(JsonNode googleCreds) {
      this.googleCreds = googleCreds;
    }

    /** Add session **/
    public UserBuilder session(Session session) {
      this.session = session;
      return this;
    }

  }

}
