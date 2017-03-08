package podcast.models.entities;

import com.couchbase.client.java.document.json.JsonObject;
import com.fasterxml.uuid.Generators;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.JsonNode;
import podcast.models.utils.Constants;

/**
 * App user (w/Google credentials)
 */
public class User extends Entity {

  @Getter private Constants.Type type = Constants.Type.USER;
  @Getter private String id;
  @Getter private String googleId;
  @Getter private String email;
  @Getter private String firstName;
  @Getter private String lastName;
  @Getter private String imageUrl;
  @Getter private Integer numberFollowers;
  @Getter private Integer numberFollowing;
  @Setter @Getter private Session session;
  @Setter @Getter private String username;

  /**
   * Constructor from Google Sign In credentials
   * @param googleCreds - JSON from Google
   */
  public User(JsonNode googleCreds) {
    /* ID */
    this.id = Generators.timeBasedGenerator().generate().toString();

    /* Google credentials */
    this.googleId = googleCreds.get("sub").asText();
    this.email = googleCreds.get("email").asText().toLowerCase();
    this.firstName = googleCreds.get("given_name") != null ?
      googleCreds.get("given_name").asText() : null;
    this.lastName = googleCreds.get("family_name") != null ?
      googleCreds.get("family_name").asText() : null;
    this.imageUrl = googleCreds.get("picture") != null ?
      googleCreds.get("picture").asText() : null;

    /* Other fields */
    this.session = null;
    this.numberFollowers = 0;
    this.numberFollowing = 0;

    /* Generate */
    this.username = "user-" + this.id;
  }


  /**
   * Constructor from Couchbase JsonObject
   * @param object - JsonObject from Couchbase
   */
  public User(JsonObject object) {
    this.id = object.getString("id");
    this.googleId = object.getString("googleID");
    this.email = object.getString("email");
    this.firstName = object.getString("firstName");
    this.lastName = object.getString("lastName");
    this.imageUrl = object.getString("imageUrl");
    this.session = new Session(object.getObject("session"));
    this.numberFollowers = object.getInt("numberFollowers");
    this.numberFollowing = object.getInt("numberFollowing");
    this.username = object.getString("username");
  }


  /**
   * See {@link Entity#toJsonObject()}
   */
  public JsonObject toJsonObject() {
    JsonObject result = JsonObject.create();
    result.put("type", type.toString());
    result.put("id", id);
    result.put("googleID", googleId);
    result.put("email", email);
    result.put("firstName", firstName);
    result.put("lastName", lastName);
    result.put("imageUrl", imageUrl);
    result.put("username", username);
    result.put("session", session.toJsonObject());
    result.put("numberFollowers", numberFollowers);
    result.put("numberFollowing", numberFollowing);
    return result;
  }

}
