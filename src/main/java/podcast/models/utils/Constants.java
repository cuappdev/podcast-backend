package podcast.models.utils;


import podcast.models.entities.User;

public class Constants {

  /* RESOURCE NAMES */
  public static final String USER = "user";
  public static final String SESSION = "session";
  public static final String SERIES = "series";
  public static final String EPISODE = "episode";
  public static final String FOLLOWER = "follower";
  public static final String FOLLOWING = "following";
  public static final String RELEASE = "release";
  public static final String RECOMMENDATION = "recommendation";

  /* FIELDS */
  public static final String TYPE = "type";
  public static final String ID = "id";
  public static final String GOOGLE_ID = "googleId";
  public static final String EMAIL = "email";
  public static final String FIRST_NAME = "firstName";
  public static final String LAST_NAME = "lastName";
  public static final String IMAGE_URL = "imageUrl";
  public static final String NUMBER_FOLLOWERS = "numberFollowers";
  public static final String NUMBER_FOLLOWING = "numberFollowing";
  public static final String USERNAME = "username";
  public static final String SESSION_TOKEN = "sessionToken";
  public static final String EXPIRES_AT = "expiresAt";
  public static final String UPDATE_TOKEN = "updateToken";
  public static final String NEW_USER = "newUser";
  public static final String ID_TOKEN = "idToken";

  // TODO - more



  /* Secret encryption key */
  // public static final String SECRET_KEY = System.getenv("SECRET_KEY");
  public static final String SECRET_KEY = "abcdefghijklmnop";

  /* Number of bytes in a UUID */
  public static final Integer UUID_BYTES = 36;

  /** Types of various entities **/
  public static enum Type {
    USER,
    SESSION,
    SERIES,
    EPISODE,
    FOLLOWER,
    FOLLOWING,
    RELEASE,
    RECOMMENDATION;

    /** Properly format for storage in Couchbase **/
    @Override
    public String toString() {
      switch (this) {
        case USER: return Constants.USER;
        case SESSION: return Constants.SESSION;
        case EPISODE: return Constants.EPISODE;
        case SERIES: return Constants.SERIES;
        case FOLLOWER: return Constants.FOLLOWER;
        case FOLLOWING: return Constants.FOLLOWING;
        case RELEASE: return Constants.RELEASE;
        case RECOMMENDATION: return Constants.RECOMMENDATION;
        default: throw new IllegalArgumentException();
      }
    }
  }


}
