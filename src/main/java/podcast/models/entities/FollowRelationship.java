package podcast.models.entities;

import com.couchbase.client.java.document.json.JsonObject;
import lombok.Getter;
import static podcast.utils.Constants.*;

/**
 * Abstract parent of followers / followings (relationships)
 */
public abstract class FollowRelationship extends Entity{
  @Getter protected Type type;
  @Getter protected String ownerId;
  @Getter protected String id;
  @Getter protected String firstName;
  @Getter protected String lastName;
  @Getter protected String username;
  @Getter protected String imageUrl;

  /** Constructor **/
  public FollowRelationship(Type type,
                            String ownerId,
                            String id,
                            String firstName,
                            String lastName,
                            String username,
                            String imageUrl) {
    this.type = type;
    this.ownerId = ownerId;
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.username = username;
    this.imageUrl = imageUrl;
  }

  /**
   * See {@link Entity#toJsonObject()}
   */
  public JsonObject toJsonObject() {
    return JsonObject.create()
      .put(TYPE, type.toString())
      .put(OWNER_ID, ownerId)
      .put(ID, id)
      .put(FIRST_NAME, firstName)
      .put(LAST_NAME, lastName)
      .put(IMAGE_URL, imageUrl)
      .put(USERNAME, username);
  }
}
