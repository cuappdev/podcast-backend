package podcast.models.entities;

import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import org.codehaus.jackson.map.ObjectMapper;
import java.util.Map;

/**
 * An Entity stored in Couchbase
 */
public abstract class Entity {

  private ObjectMapper mapper = new ObjectMapper();

  /**
   * Convert this into a JsonDocument for storage in Couchbase
   * @return - Couchbase JsonDocument
   */
  public abstract JsonDocument toJsonDocument();

  /**
   * To JsonObject, via converting to a map
   * @return - Couchbase JsonObject
   */
  public JsonObject toJsonObject() {
    Map<String, Object> props = mapper.convertValue(this, Map.class);
    return JsonObject.from(props);
  }

  /**
   * Base composeKey function
   * @param key - Key to attach type info to
   * @param type - Type information in the form of a String
   * @return - Key string
   */
  protected static String composeKey(String key, String type) {
    return String.format("%s:%s", key, type);
  }

}
