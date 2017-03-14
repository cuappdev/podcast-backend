package podcast.models.entities;

import com.couchbase.client.java.document.json.JsonObject;
import lombok.Getter;
import podcast.utils.Constants;

/**
 * Abstract parent of followers / followings (relationships)
 */
public abstract class FollowRelationship extends Entity{
  @Getter private Constants.Type type;
  @Getter protected String ownerId;
  @Getter protected String id;
  @Getter protected String firstName;
  @Getter protected String lastName;
  @Getter protected String username;
  @Getter protected String imageUrl;

  /**
   * See {@link Entity#toJsonObject()}
   */
  public JsonObject toJsonObject() {
    JsonObject result = JsonObject.create();
    result.put(Constants.TYPE, type.toString());
    result.put(Constants.ID, id);
    result.put(Constants.FIRST_NAME, firstName);
    result.put(Constants.LAST_NAME, lastName);
    result.put(Constants.IMAGE_URL, imageUrl);
    result.put(Constants.USERNAME, username);
    return result;
  }
}
