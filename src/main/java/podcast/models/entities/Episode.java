package podcast.models.entities;

import com.couchbase.client.java.document.json.JsonObject;

import java.util.Date;

/**
 * Podcast episode (e.g. an episode of 'Serial')
 */
public class Episode extends Podcast {


  public Episode(JsonObject object) {

  }

  /**
   * See {@link Entity#toJsonObject()}
   */
  public JsonObject toJsonObject() {
    // TODO
    return null;
  }

}
