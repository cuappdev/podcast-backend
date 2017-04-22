package podcast.models.entities.followings;

import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import lombok.Getter;
import podcast.models.entities.Entity;

import static podcast.utils.Constants.*;

/**
 * Abstract parent of followers / followings (relationships)
 */
public abstract class FollowRelationship extends Entity {

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


  /** See {@link Entity#toJsonDocument()} **/
  protected JsonDocument toJsonDocument(String key) {
    return JsonDocument.create(key, super.toJsonObject());
  }

  /** When the relationship is not found **/
  public static class NonExistentFollowingException extends Exception {
    public NonExistentFollowingException() {
      super("No following relationship exists of this nature");
    }
  }

}
