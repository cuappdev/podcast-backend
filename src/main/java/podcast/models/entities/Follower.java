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
  @Getter private String id;
  @Getter private String firstName;
  @Getter private String lastName;
  @Getter private String username;
  @Getter private String imageUrl;


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


  /**
   * See {@link Entity#toJsonObject()}
   */
  public JsonObject toJsonObject() {
    // TODO
    return null;
  }

}
