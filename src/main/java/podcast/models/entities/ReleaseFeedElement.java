package podcast.models.entities;

import com.couchbase.client.java.document.json.JsonObject;
import lombok.Getter;
import podcast.utils.Constants;

/**
 * New podcast episode release feed element
 */
public class ReleaseFeedElement extends FeedElement {

  @Getter private Constants.Type type = Constants.Type.RELEASE;
  @Getter private Episode episode;

  /**
   * Constructor from Episode
   * @param episode - Episode
   */
  public ReleaseFeedElement(Episode episode) {
    this.episode = episode;
  }


  /**
   * See {@link Entity#toJsonObject()}
   */
  public JsonObject toJsonObject() {
    // TODO
    return null;
  }


}
