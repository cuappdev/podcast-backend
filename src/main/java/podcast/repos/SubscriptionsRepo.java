package podcast.repos;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.JsonDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import podcast.models.entities.Subscription;

public class SubscriptionsRepo {

  private Bucket bucket;

  @Autowired
  public SubscriptionsRepo(@Qualifier("subscriptionsBucket") Bucket subscriptionsBucket) {
    this.bucket = subscriptionsBucket;
  }

  /**
   * Key is SeriesID:UserId
   * @param s
   * @return
   */
  public String composeKey(Subscription s) {
    return s.getSeriesId() + ":" + s.getUser().getId();
  }

  /** Stores a subscription **/
  public Subscription storeSubscription(Subscription subscription) {
    JsonDocument doc = JsonDocument.create(composeKey(subscription), subscription.toJsonObject());
    bucket.upsert(doc);
    return subscription;
  }
}
