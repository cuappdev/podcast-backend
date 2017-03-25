package podcast.models.entities;

import com.couchbase.client.java.document.JsonDocument;

/**
 * An Entity stored in Couchbase
 */
public abstract class Entity {

  /**
   * Convert this into a JsonDocument for storage in Couchbase
   * @return - Couchbase JsonDocument
   */
  public abstract JsonDocument toJsonDocument();

}
