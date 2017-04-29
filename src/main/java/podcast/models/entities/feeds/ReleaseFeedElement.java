package podcast.models.entities.feeds;

import com.couchbase.client.java.document.JsonDocument;
import lombok.Getter;
import podcast.models.entities.Entity;
import podcast.models.entities.podcasts.Episode;
import podcast.utils.Constants;

/**
 * New podcast episode release feed element
 */
public class ReleaseFeedElement extends FeedElement {

  @Getter private Constants.Type type = Constants.Type.releaseFeedElement;
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
