package podcast.repos;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.JsonDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import podcast.models.entities.Subscription;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import podcast.models.entities.User;
import rx.Observable;

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
    JsonDocument doc = subscription.toJsonDocument();
    bucket.upsert(doc);
    return subscription;
  }


  public boolean deleteSubscription(Subscription subscription) {
    List<Object> keys = Arrays.asList(
      Subscription.composeKey(subscription)
    );
    Observable
        .from(keys)
        .flatMap(x -> {
          if(x instanceof String) {
            return bucket.async().remove((String) x);
          }
          else {
            return bucket.async().remove((JsonDocument) x);
          }
        })
        .last()
        .toBlocking()
        .single();
    return true;
  }

  public List<Subscription> getUserSubscriptions(User user) {
    return new ArrayList<Subscription>();
  }
}
