package podcast.models.entities;

import com.couchbase.client.java.document.json.JsonObject;
import lombok.Getter;
import podcast.models.utils.Constants;

/**
 * Follower (someone following you)
 */
public class Follower extends Entity {

  @Getter private Constants.Type type = Constants.Type.FOLLOWER;
  @Getter private String ownerId;
  @Getter private String followerId;
  @Getter private String followerFirstName;
  @Getter private String followerLastName;
  @Getter private String followerUsername;
  @Getter private String followerImageUrl;


  /**
   * Constructor from owning User and future follower
   * @param owner - User
   * @param follower - User
   */
  public Follower(User owner, User follower) {
    this.ownerId = owner.getId();
    this.followerId = follower.getId();
    this.followerFirstName = follower.getFirstName();
    this.followerLastName = follower.getLastName();
    this.followerUsername = follower.getUsername();
    this.followerImageUrl = follower.getImageUrl();
  }


  /**
   * See {@link Entity#toJsonObject()}
   */
  public JsonObject toJsonObject() {
    // TODO
    return null;
  }

}
