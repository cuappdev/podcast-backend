package podcast.models.utils;


public class Constants {

  // Secret encryption key
  public static String SECRET_KEY = System.getenv("SECRET_KEY");

  public static enum Type {
    FOLLOWER, FOLLOWING
  }
}
