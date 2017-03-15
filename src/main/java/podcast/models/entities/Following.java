package podcast.models.entities;

import com.couchbase.client.java.document.json.JsonObject;
import static podcast.utils.Constants.*;

/**
 * Following (someone you follow)
 */
public class Following extends FollowRelationship {

  /**
   * Constructor from owning User and future following
   * @param owner - User
   * @param following - User
   */
  public Following(User owner, User following) {
    super(
      Type.FOLLOWING,
      owner.getId(),
      following.getId(),
      following.getFirstName(),
      following.getLastName(),
      following.getUsername(),
      following.getImageUrl());
  }

  public Following(JsonObject object) {
    super(
      Type.FOLLOWING,
      object.getString(OWNER_ID),
      object.getString(ID),
      object.getString(FIRST_NAME),
      object.getString(LAST_NAME),
      object.getString(USERNAME),
      object.getString(IMAGE_URL)
    );
  }

}
