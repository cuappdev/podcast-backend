package podcast.models.entities;

import com.couchbase.client.java.document.json.JsonObject;
import lombok.Getter;
import podcast.models.utils.Constants;
import podcast.models.utils.Crypto;
import podcast.models.utils.TokenGenerator;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;

/**
 * Session used to authenticate users on REST API calls
 */
public class Session extends Entity {

  /* Fields */
  @Getter private Constants.Type type = Constants.Type.SESSION;
  @Getter private String sessionToken;
  @Getter private Date expiresAt;
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
    return new String(Arrays.copyOfRange(decrypted.getBytes(), 0, Constants.UUID_BYTES));
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
    this.expiresAt = java.sql.Date.valueOf(nextWeek);

    /* Update Token */
    this.updateToken = Session.tokenFromUser(user);
  }


  /**
   * Constructor from Couchbase JsonObject
   * @param object - JsonObject from Couchbase
   */
  public Session(JsonObject object) {
    this.sessionToken = object.getString(Constants.SESSION_TOKEN);
    this.expiresAt = new Date(object.getLong(Constants.EXPIRES_AT));
    this.updateToken = object.getString(Constants.UPDATE_TOKEN);
  }


  /**
   * See {@link Entity#toJsonObject()}
   */
  public JsonObject toJsonObject() {
    JsonObject result = JsonObject.create();
    result.put(Constants.TYPE, type.toString());
    result.put(Constants.SESSION_TOKEN, sessionToken);
    result.put(Constants.EXPIRES_AT, expiresAt.getTime()); // Store long
    result.put(Constants.UPDATE_TOKEN, updateToken);
    return result;
  }

}
