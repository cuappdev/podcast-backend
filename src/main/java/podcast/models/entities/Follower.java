package podcast.models.entities;

import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import static podcast.utils.Constants.*;

/**
 * Follower (someone following you)
 */
public class Follower extends FollowRelationship {

  /**
   * Constructor from owning User and future follower
   * @param owner - User
   * @param follower - User
   */
  public Follower(User owner, User follower) {
    super(
      Type.follower,
      owner.getId(),
      follower.getId(),
      follower.getFirstName(),
      follower.getLastName(),
      follower.getUsername(),
      follower.getImageUrl());
  }

  /** Constructor from JsonObject **/
  public Follower(JsonObject object) {
    super(
      Type.follower,
      object.getString(OWNER_ID),
      object.getString(ID),
      object.getString(FIRST_NAME),
      object.getString(LAST_NAME),
      object.getString(USERNAME),
      object.getString(IMAGE_URL)
    );
  }

  /** See {@link Entity#toJsonDocument()} **/
  public JsonDocument toJsonDocument() {
    return super.toJsonDocument(composeKey(this));
  }

}
