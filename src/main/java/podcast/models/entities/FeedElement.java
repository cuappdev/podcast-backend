package podcast.models.entities;

import lombok.Getter;

/**
 * FeedElement base abstract class
 */
public abstract class FeedElement extends Entity {

  /** Different types of FeedElement's **/
  public enum FeedElementType { RELEASE, RECOMMENDATION }

  @Getter protected FeedElementType type;

}
