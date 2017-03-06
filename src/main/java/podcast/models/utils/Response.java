package podcast.models.utils;

import lombok.Getter;
import java.util.HashMap;

public class Response {

  @Getter private Boolean success;
  @Getter private HashMap<String, Object> data;

  public Response(Boolean success, String key, Object value) {
    this.success = success;
    HashMap<String, Object> data = new HashMap<String, Object>();
    data.put(key, value);
    this.data = data;
  }

}
