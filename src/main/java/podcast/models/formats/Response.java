package podcast.models.formats;

import lombok.Getter;
import java.util.HashMap;

public class Response {

  @Getter private Boolean success;
  @Getter private HashMap<String, Object> data;

  /** Constructor **/
  public Response(Boolean success, String key, Object value) {
    this.success = success;
    HashMap<String, Object> data = new HashMap<String, Object>();
    data.put(key, value);
    this.data = data;
  }

  /** Add more information to data **/
  public void addField(String key, Object value) {
    this.data.put(key, value);
  }

}
