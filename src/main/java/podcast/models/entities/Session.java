package podcast.models.entities;

import com.couchbase.client.java.document.json.JsonObject;
import lombok.Getter;
import podcast.models.utils.Crypto;
import podcast.models.utils.TokenGenerator;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * Session used to authenticate users on REST API calls
 */
public class Session {

  /* Fields */
  @Getter private String sessionToken;
  @Getter private Date expiresAt;
  @Getter private String updateToken;

  /**
   * Standard constructor
   * @param user - The owning user of this session
   */
  public Session(User user) {

    /* Session Token */
    StringBuilder SB = new StringBuilder();
    SB.append(Crypto.encryptUUID(user.getUuid()));
    SB.append(TokenGenerator.urlSafeRandomToken());
    this.sessionToken = SB.toString();

    /* Expires At */
    LocalDate nextWeek = LocalDate.now().plus(1, ChronoUnit.WEEKS);
    this.expiresAt = java.sql.Date.valueOf(nextWeek);

    /* Update Token */
    this.updateToken = TokenGenerator.urlSafeRandomToken();

  }

  /**
   * Constructor from owning User and JsonObject
   * @param object - JsonObject
   */
  public Session(JsonObject object) {
    this.sessionToken = object.getString("sessionToken");
    this.expiresAt = new Date(object.getLong("expiresAt"));
    this.updateToken = object.getString("updateToken");
  }

  /**
   * Convert this user into a JsonObject
   * @return JsonObject
   */
  public JsonObject toJsonObject() {
    JsonObject result = JsonObject.create();
    result.put("sessionToken", sessionToken);
    result.put("expiresAt", expiresAt.getTime()); // Store long
    result.put("updateToken", updateToken);
    return result;
  }

}
