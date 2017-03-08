package podcast.models.entities;

import com.couchbase.client.java.document.json.JsonObject;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * An Entity stored in Couchbase
 */
@JsonIgnoreProperties("type")
public abstract class Entity {

  /**
   * Convert this into a JsonObject for storage in Couchbase
   * @return - Couchbase JsonObject
   */
  public abstract JsonObject toJsonObject();

}
