package podcast.models.entities;

/**
 * FeedElement base abstract class
 */
public abstract class FeedElement {

  /** Different types of FeedElement's **/
  public enum FeedElementType {
    NEWPODCAST,
    RECOMMENDATION,
    SUBSCRIPTION
  }


}
