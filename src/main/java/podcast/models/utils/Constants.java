package podcast.models.utils;


public class Constants {

  /* Secret encryption key */
  // public static final String SECRET_KEY = System.getenv("SECRET_KEY");
  public static final String SECRET_KEY = "abcdefghijklmnop";

  /* Number of bytes in a UUID */
  public static final Integer UUID_BYTES = 36;

  /** Types of various entities **/
  public static enum Type {
    USER,
    SESSION,
    EPISODE,
    SERIES,
    FOLLOWER,
    FOLLOWING,
    RELEASE,
    RECOMMENDATION;

    /** Properly format for storage in Couchbase **/
    @Override
    public String toString() {
      switch (this) {
        case USER: return "user";
        case SESSION: return "session";
        case EPISODE: return "episode";
        case SERIES: return "series";
        case FOLLOWER: return "follower";
        case FOLLOWING: return "following";
        case RELEASE: return "release";
        case RECOMMENDATION: return "recommendation";
        default: throw new IllegalArgumentException();
      }
    }
  }

}
