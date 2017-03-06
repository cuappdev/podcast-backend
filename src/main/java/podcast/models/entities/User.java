package podcast.models.entities;

import com.couchbase.client.java.document.json.JsonObject;
import com.fasterxml.uuid.Generators;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.JsonNode;

/**
 * App user (w/Google credentials)
 */
public class User extends Entity {

  @Getter private String uuid;
  @Getter private String googleID;
  @Getter private String email;
  @Getter private String firstName;
  @Getter private String lastName;
  @Getter private String imageURL;
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
    this.uuid = Generators.timeBasedGenerator().generate().toString();

    /* Google credentials */
    this.googleID = googleCreds.get("sub").asText();
    this.email = googleCreds.get("email").asText().toLowerCase();
    this.firstName = googleCreds.get("given_name") != null ?
      googleCreds.get("given_name").asText() : null;
    this.lastName = googleCreds.get("family_name") != null ?
      googleCreds.get("family_name").asText() : null;
    this.imageURL = googleCreds.get("picture") != null ?
      googleCreds.get("picture").asText() : null;

    /* Other fields */
    this.session = null;
    this.numberFollowers = 0;
    this.numberFollowing = 0;

    /* Generate */
    this.username = "user-" + this.uuid;
  }


  /**
   * Constructor from Couchbase JsonObject
   * @param object - JsonObject from Couchbase
   */
  public User(JsonObject object) {
    this.uuid = object.getString("uuid");
    this.googleID = object.getString("googleID");
    this.email = object.getString("email");
    this.firstName = object.getString("firstName");
    this.lastName = object.getString("lastName");
    this.imageURL = object.getString("imageURL");
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
    return result;
  }

}
