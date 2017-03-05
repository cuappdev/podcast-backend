package podcast.models.utils;

import lombok.Getter;
import podcast.models.entities.Entity;
import java.util.HashMap;

public class Response {

  @Getter private Boolean success;
  @Getter private HashMap<String, Entity> data;

  public Response(Boolean success, String key, Entity value) {
    this.success = success;
    HashMap<String, Entity> data = new HashMap<String, Entity>();
    data.put(key, value);
    this.data = data;
  }

}
