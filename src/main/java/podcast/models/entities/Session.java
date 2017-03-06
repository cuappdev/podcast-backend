package podcast.models.entities;

import com.couchbase.client.java.document.json.JsonObject;

public class Session extends Entity {

  /**
   * Constructor
   * @param user - User
   */
  public Session(User user) {
    // TODO
  }

  /**
   * Constructor from Couchbase JsonObject
   * @param object - JsonObject
   */
  public Session(JsonObject object) {
    // TODO
  }

  /**
   * See {@link Entity#toJsonObject()}
   */
  public JsonObject toJsonObject() {
    // TODO
    return null;
  }

}
