package podcast.models.formats;

import lombok.Getter;
import java.util.HashMap;

/**
 * Success response format
 */
public class Success extends Result {

  @Getter private Boolean success = true;
  @Getter private HashMap<String, Object> data;

  /** Constructor **/
  public Success(String key, Object value) {
    HashMap<String, Object> data = new HashMap<String, Object>();
    data.put(key, value);
    this.data = data;
  }

  /** Add more information to data **/
  public void addField(String key, Object value) {
    this.data.put(key, value);
  }

}
