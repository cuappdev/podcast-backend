package podcast.models.entities;

import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.fasterxml.uuid.Generators;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.JsonNode;
import java.util.ArrayList;
import java.util.List;
import static podcast.utils.Constants.*;

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


  /** Make GoogleIdToUser lookup entity **/
  public GoogleIdToUser makeGoogleIdToUser() {
    return new GoogleIdToUser(getGoogleId(), getId());
  }


  /** Make UsernameToUser lookup entity **/
  public UsernameToUser makeUsernameToUser() {
    return new UsernameToUser(getUsername(), getId());
  }


  /** User documents to be inserted **/
  public List<JsonDocument> docs() {
    List<JsonDocument> docs = new ArrayList<JsonDocument>();
    docs.add(JsonDocument.create(getId(), toJsonObject()));
    docs.add(JsonDocument.create(getUsername(), makeUsernameToUser().toJsonObject()));
    if (getGoogleId() != null) {
      docs.add(JsonDocument.create(getGoogleId(), makeGoogleIdToUser().toJsonObject()));
    }
    // TODO check fb
    return docs;
  }


  /** Keys **/
  public List<String> keys() {
    List<String> keys = new ArrayList<String>();
    keys.add(getId());
    keys.add(getUsername());
    if (getGoogleId() != null) {
      keys.add(getGoogleId());
    }
    // TODO check fb
    return keys;
  }


  /** Thrown when the username is not valid **/
  public static class InvalidUsernameException extends Exception {
    public InvalidUsernameException() {
      super("This username is not valid, please try again.");
    }
  }


  /** Thrown when the username is taken **/
  public static class UsernameTakenException extends Exception {
    public UsernameTakenException() {
      super("This username is taken, please choose another one");
    }
  }


  /**
   * Various types that can be used to lookup entities in the DB
   */
  public static enum UserLookupType {
    USERNAME_TO_USER,
    GOOGLE_ID_TO_USER,
    FACEBOOK_ID_TO_USER
    // TODO - More?
  }

  /**
   * Entity articulating the pairing of a
   * Username to a User
   */
  public static class UsernameToUser extends Entity {

    @Getter private UserLookupType type = UserLookupType.USERNAME_TO_USER;
    @Getter private String username;
    @Getter private String userId;

    /** Constructor **/
    private UsernameToUser(String username, String userId) {
      this.username = username.toLowerCase();
      this.userId = userId;
    }

    /** Constructor from JsonObject **/
    public UsernameToUser(JsonObject object) {
      this.username = object.getString(USERNAME);
      this.userId = object.getString(USER_ID);
    }

    /** See {@link Entity#toJsonObject()} **/
    public JsonObject toJsonObject() {
      return JsonObject.create()
        .put(TYPE, type.toString())
        .put(USERNAME, username)
        .put(USER_ID, userId);
    }
  }

  /**
   * Entity articulating the pairing of a
   * GoogleId to a User
   */
  public static class GoogleIdToUser extends Entity {

    @Getter private UserLookupType type = UserLookupType.GOOGLE_ID_TO_USER;
    @Getter private String googleId;
    @Getter private String userId;

    /** Constructor **/
    private GoogleIdToUser(String googleId, String userId) {
      this.googleId = googleId;
      this.userId = userId;
    }

    /** Constructor from JsonObject **/
    public GoogleIdToUser(JsonObject object) {
      this.googleId = object.getString(GOOGLE_ID);
      this.userId = object.getString(USER_ID);
    }

    /** See {@link Entity#toJsonObject()} **/
    public JsonObject toJsonObject() {
      return JsonObject.create()
        .put(TYPE, type.toString())
        .put(GOOGLE_ID, googleId)
        .put(USER_ID, userId);
    }
  }

  /**
   * Entity articulating the pairing of a
   * FacebookId to a User
   */
  public static class FacebookIdToUser extends Entity {
    /** See {@link Entity#toJsonObject()} **/
    public JsonObject toJsonObject() {
      // TODO
      return JsonObject.create();
    }
  }

}
