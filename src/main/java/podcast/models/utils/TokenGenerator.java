package podcast.models.utils;

import java.security.SecureRandom;
import java.util.Base64;

public class TokenGenerator {

  /**
   * Generates a URL-safe random token that's 20 bytes in size
   * (citing: https://goo.gl/ziO6sE)
   * @return - String (token)
   */
  public static String urlSafeRandomToken() {
    SecureRandom random = new SecureRandom();
    byte bytes[] = new byte[20];
    random.nextBytes(bytes);
    Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
    String token = encoder.encodeToString(bytes);
    return token;
  }


}