package podcast.repos;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.JsonDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import podcast.models.entities.Series;
import podcast.models.entities.Subscription;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import podcast.models.entities.User;
import rx.Observable;

@Component
public class SubscriptionsRepo {

  private Bucket bucket;

  @Autowired
  public SubscriptionsRepo(@Qualifier("dbBucket") Bucket subscriptionsBucket) {
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
  public Subscription storeSubscription(Subscription subscription, Series series) {
    series.incrementSubscriberCount();
    List<Object> keys = Arrays.asList(
      Subscription.composeKey(subscription),
      Series.composeKey(series.getId(), new Long(0))
    );
    Observable
        .from(keys)
        .flatMap(x -> {
          return bucket.async().upsert((JsonDocument) x);
        })
        .last()
        .toBlocking()
        .single();
    return subscription;
  }


  public boolean deleteSubscription(Subscription subscription, Series series) {
    series.decrementSubscriberCount();
    List<Object> keys = Arrays.asList(
      Subscription.composeKey(subscription),
      Series.composeKey(series.getId(), new Long(0)) // TODO - figure out what to do here - series don't have pubdates
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
