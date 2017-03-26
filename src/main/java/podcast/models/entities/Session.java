package podcast.models.entities;

import com.couchbase.client.java.document.json.JsonObject;
import lombok.Getter;
import org.codehaus.jackson.map.ObjectMapper;
import podcast.models.utils.Crypto;
import podcast.models.utils.TokenGenerator;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import static podcast.utils.Constants.*;

/**
 * Session used to authenticate users on REST API calls
 */
public class Session {

  /* Fields */
  @Getter private Type type = Type.session;
  @Getter private String sessionToken;
  @Getter private Long expiresAt;
  @Getter private String updateToken;


  /** Generate a pseudo-random token from user **/
  private static String tokenFromUser(User user) {
    StringBuilder SB = new StringBuilder();
    SB.append(user.getId());
    SB.append(TokenGenerator.urlSafeRandomToken());
    return Crypto.encrypt(SB.toString());
  }


  /** Get user ID from token **/
  public static String tokenToUserId(String token) {
    String decrypted = Crypto.decrypt(token);
    return new String(Arrays.copyOfRange(decrypted.getBytes(), 0, UUID_BYTES));
  }


  /**
   * Constructor from owning user
   * @param user - owning user
   */
  public Session(User user) {

    /* Session Token */
    this.sessionToken = Session.tokenFromUser(user);

    /* Expires At */
    LocalDate nextWeek = LocalDate.now().plus(1, ChronoUnit.WEEKS);
    this.expiresAt = java.sql.Date.valueOf(nextWeek).getTime() / 1000;

    /* Update Token */
    this.updateToken = Session.tokenFromUser(user);
  }


  /**
   * Constructor from Couchbase JsonObject
   * @param object - JsonObject from Couchbase
   */
  public Session(JsonObject object) {
    this.sessionToken = object.getString(SESSION_TOKEN);
    this.expiresAt = object.getLong(EXPIRES_AT);
    this.updateToken = object.getString(UPDATE_TOKEN);
  }


  /** To JsonObject **/
  public JsonObject toJsonObject() {
    ObjectMapper mapper = new ObjectMapper();
    return JsonObject.from(mapper.convertValue(this, Map.class));
  }


}
