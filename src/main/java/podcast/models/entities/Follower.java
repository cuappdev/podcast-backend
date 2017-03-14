package podcast.models.entities;

import com.couchbase.client.java.document.json.JsonObject;
import lombok.Getter;
import podcast.utils.Constants;

/**
 * Follower (someone following you)
 */
public class Follower extends FollowRelationship {
  @Getter private Constants.Type type = Constants.Type.FOLLOWER;

  /**
   * Constructor from owning User and future follower
   * @param owner - User
   * @param follower - User
   */
  public Follower(User owner, User follower) {
    this.ownerId = owner.getId();
    this.id = follower.getId();
    this.firstName = follower.getFirstName();
    this.lastName = follower.getLastName();
    this.username = follower.getUsername();
    this.imageUrl = follower.getImageUrl();
  }

  public Follower(JsonObject object) {
    this.id = object.getString(Constants.ID);
    this.firstName = object.getString(Constants.FIRST_NAME);
    this.lastName = object.getString(Constants.LAST_NAME);
    this.imageUrl = object.getString(Constants.IMAGE_URL);
    this.username = object.getString(Constants.USERNAME);
  }

}
