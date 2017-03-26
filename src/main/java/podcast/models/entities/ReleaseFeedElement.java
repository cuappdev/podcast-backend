package podcast.models.entities;

import com.couchbase.client.java.document.JsonDocument;
import lombok.Getter;
import podcast.utils.Constants;

/**
 * New podcast episode release feed element
 */
public class ReleaseFeedElement extends FeedElement {

  @Getter private Constants.Type type = Constants.Type.releaseElement;
  @Getter private Episode episode;

  /**
   * Constructor from Episode
   * @param episode - Episode
   */
  public ReleaseFeedElement(Episode episode) {
    this.episode = episode;
  }


  /**
   * See {@link Entity#toJsonDocument()}
   */
  public JsonDocument toJsonDocument() {
    // TODO
    return null;
  }


}
