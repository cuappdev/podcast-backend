package podcast.models.entities;

import com.couchbase.client.java.document.json.JsonObject;

/**
 * An Entity stored in Couchbase
 */
public abstract class Entity {

  /**
   * Convert this into a JsonObject for storage in Couchbase
   * @return - Couchbase JsonObject
   */
  public abstract JsonObject toJsonObject();

}
