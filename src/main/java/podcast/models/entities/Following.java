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
  @Getter private String id;
  @Getter private String firstName;
  @Getter private String lastName;
  @Getter private String username;
  @Getter private String imageUrl;

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


  /**
   * See {@link Entity#toJsonObject()}
   */
  public JsonObject toJsonObject() {
    // TODO
    return null;
  }

}
