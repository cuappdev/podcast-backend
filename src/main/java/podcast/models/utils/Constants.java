package podcast.models.utils;


public class Constants {

  /* Secret encryption key */
  public static final String SECRET_KEY = System.getenv("SECRET_KEY");


  /** Types of various entities **/
  public static enum Type {
    USER,
    EPISODE,
    SERIES,
    FOLLOWER,
    FOLLOWING;

    /** Properly format for storage in Couchbase **/
    @Override
    public String toString() {
      switch (this) {
        case USER: return "user";
        case EPISODE: return "episode";
        case SERIES: return "series";
        case FOLLOWER: return "follower";
        case FOLLOWING: return "following";
        default: throw new IllegalArgumentException();
      }
    }
  }

}
