package podcast.models.entities;

import com.couchbase.client.java.document.json.JsonObject;
import lombok.Getter;
import podcast.models.utils.Constants;

/**
 * Following (someone you follow)
 */
public class Following extends Entity {

  @Getter private Constants.Type type = Constants.Type.FOLLOWING;
  @Getter private String ownerId;
  @Getter private String followingId;
  @Getter private String followingFirstName;
  @Getter private String followingLastName;
  @Getter private String followingUsername;
  @Getter private String followingImageUrl;

  /**
   * Constructor from owning User and future following
   * @param owner - User
   * @param following - User
   */
  public Following(User owner, User following) {
    this.ownerId = owner.getId();
    this.followingId = following.getId();
    this.followingFirstName = following.getFirstName();
    this.followingLastName = following.getLastName();
    this.followingUsername = following.getUsername();
    this.followingImageUrl = following.getImageUrl();
  }


  /**
   * See {@link Entity#toJsonObject()}
   */
  public JsonObject toJsonObject() {
    // TODO
    return null;
  }

}
