package podcast.models.entities.users;

import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.fasterxml.uuid.Generators;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.JsonNode;
import podcast.models.entities.Entity;
import podcast.models.entities.sessions.Session;

import java.util.ArrayList;
import java.util.List;
import static podcast.utils.Constants.*;

/**
 * App user (w/Google credentials)
 */
public class User extends Entity {

  @Getter private Type type = Type.user;
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
   * See {@link Entity#toJsonDocument()}
   */
  public JsonDocument toJsonDocument() {
    return JsonDocument.create(composeKey(this), super.toJsonObject());
  }


  /** Username setter **/
  public void setUsername(String username) throws InvalidUsernameException {
    if (username.length() == 0) {
      throw new InvalidUsernameException();
    }
    this.username = username;
  }


  /** Increment followers **/
  public void incrementFollowers() {
    numberFollowers += 1;
  }


  /** Increment followings **/
  public void incrementFollowings() {
    numberFollowing += 1;
  }


  /** Decrement followers **/
  public void decrementFollowers() {
    numberFollowers -= 1;
  }


  /** Decrement followings **/
  public void decrementFollowings() {
    numberFollowing -= 1;
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
    docs.add(toJsonDocument());
    docs.add(makeUsernameToUser().toJsonDocument());
    if (getGoogleId() != null) {
      docs.add(makeGoogleIdToUser().toJsonDocument());
    }
    // TODO check fb
    return docs;
  }


  /** Keys **/
  public List<String> keys() {
    List<String> keys = new ArrayList<String>();
    keys.add(composeKey(this));
    keys.add(UsernameToUser.composeKey(getUsername()));
    if (getGoogleId() != null) {
      keys.add(GoogleIdToUser.composeKey(getGoogleId()));
    }
    // TODO check fb
    return keys;
  }

  /** Compose key from userId **/
  public static String composeKey(String userId) {
    return Entity.composeKey(userId, Type.user.toString());
  }

  /** Compose key from user **/
  public static String composeKey(User user) {
    return composeKey(user.getId());
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
    usernameToUser,
    googleIdToUser,
    facebookIdToUser
    // TODO - More?


  }

  /**
   * Entity articulating the pairing of a
   * Username to a User
   */
  public static class UsernameToUser extends Entity {

    @Getter private UserLookupType type = UserLookupType.usernameToUser;
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

    /** See {@link Entity#toJsonDocument()} **/
    public JsonDocument toJsonDocument() {
      return JsonDocument.create(composeKey(this), super.toJsonObject());
    }

    /** Compose key from username **/
    public static String composeKey(String username) {
      return Entity.composeKey(username, UserLookupType.usernameToUser.toString());
    }

    /** Compose key from object **/
    public static String composeKey(UsernameToUser utu) {
      return composeKey(utu.getUsername());
    }

  }

  /**
   * Entity articulating the pairing of a
   * GoogleId to a User
   */
  public static class GoogleIdToUser extends Entity {

    @Getter private UserLookupType type = UserLookupType.googleIdToUser;
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

    /** See {@link Entity#toJsonDocument()} **/
    public JsonDocument toJsonDocument() {
      return JsonDocument.create(composeKey(this), super.toJsonObject());
    }

    /** Compose key from googleId **/
    public static String composeKey(String googleId) {
      return Entity.composeKey(googleId, UserLookupType.googleIdToUser.toString());
    }

    /** Compose key from object **/
    public static String composeKey(GoogleIdToUser gitu) {
      return composeKey(gitu.getGoogleId());
    }

  }

  /**
   * Entity articulating the pairing of a
   * FacebookId to a User
   */
  public static class FacebookIdToUser extends Entity {
    @Getter private UserLookupType type = UserLookupType.facebookIdToUser;
    @Getter private String facebookId;
    @Getter private String userId;

    /** See {@link Entity#toJsonDocument()} **/
    public JsonDocument toJsonDocument() {
      return JsonDocument.create(composeKey(this), super.toJsonObject());
    }

    /** Compose key from facebookId **/
    public static String composeKey(String facebookId) {
      return Entity.composeKey(facebookId, UserLookupType.facebookIdToUser.toString());
    }

    /** Compose key from object **/
    public static String composeKey(FacebookIdToUser gitu) {
      return composeKey(gitu.getFacebookId());
    }
  }

}
