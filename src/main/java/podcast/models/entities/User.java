package podcast.models.entities;

import com.couchbase.client.java.document.json.JsonObject;
import com.fasterxml.uuid.Generators;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.JsonNode;
import static podcast.models.utils.Constants.*;

/**
 * App user (w/Google credentials)
 */
public class User extends Entity {

  @Getter private Type type = Type.USER;
  @Getter private String id;
  @Getter private String googleId;
  @Getter private String email;
  @Getter private String firstName;
  @Getter private String lastName;
  @Getter private String imageUrl;
  @Getter private Integer numberFollowers;
  @Getter private Integer numberFollowing;
  @Getter private String username;
  @Setter @Getter private Session session;


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
    this.id = object.getString(ID);
    this.googleId = object.getString(GOOGLE_ID);
    this.email = object.getString(EMAIL);
    this.firstName = object.getString(FIRST_NAME);
    this.lastName = object.getString(LAST_NAME);
    this.imageUrl = object.getString(IMAGE_URL);
    this.session = new Session(object.getObject(SESSION));
    this.numberFollowers = object.getInt(NUMBER_FOLLOWERS);
    this.numberFollowing = object.getInt(NUMBER_FOLLOWING);
    this.username = object.getString(USERNAME);
  }


  /**
   * See {@link Entity#toJsonObject()}
   */
  public JsonObject toJsonObject() {
    JsonObject result = JsonObject.create();
    result.put(TYPE, type.toString());
    result.put(ID, id);
    result.put(GOOGLE_ID, googleId);
    result.put(EMAIL, email);
    result.put(FIRST_NAME, firstName);
    result.put(LAST_NAME, lastName);
    result.put(IMAGE_URL, imageUrl);
    result.put(USERNAME, username);
    result.put(SESSION, session.toJsonObject());
    result.put(NUMBER_FOLLOWERS, numberFollowers);
    result.put(NUMBER_FOLLOWING, numberFollowing);
    return result;
  }


  /** Username setter **/
  public void setUsername(String username) throws InvalidUsernameException {
    if (username.length() == 0) {
      throw new InvalidUsernameException();
    }
    this.username = username;
  }


  /** Thrown when the username is not valid **/
  public class InvalidUsernameException extends Exception {
    public InvalidUsernameException() {
      super("This username is not valid, please try again.");
    }
  }

}
