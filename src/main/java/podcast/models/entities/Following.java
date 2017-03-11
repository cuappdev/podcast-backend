package podcast.models.entities;


import com.couchbase.client.java.document.json.JsonObject;
import lombok.Getter;
import podcast.models.utils.Constants;

/**
 * Following (someone you follow)
 */
public class Following extends FollowRelationship {
  @Getter private Constants.Type type = Constants.Type.FOLLOWING;

  /**
   * Constructor from owning User and future following
   * @param owner - User
   * @param following - User
   */
  public Following(User owner, User following) {
    this.ownerId = owner.getId();
    this.id = following.getId();
    this.firstName = following.getFirstName();
    this.lastName = following.getLastName();
    this.username = following.getUsername();
    this.imageUrl = following.getImageUrl();
  }

}
