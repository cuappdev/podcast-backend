package podcast.models.entities.feeds;

import lombok.Getter;
import podcast.models.entities.Entity;
import podcast.utils.Constants;

/**
 * FeedElement base abstract class
 */
public abstract class FeedElement extends Entity {

  @Getter private Constants.Type type = Constants.Type.feed;

  /** Enum specifying different feed-types **/
  public static enum FeedType {
    recommendationFeedElement,
    releaseFeedElement,
  }

}
