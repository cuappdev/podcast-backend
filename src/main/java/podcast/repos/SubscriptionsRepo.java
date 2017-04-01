package podcast.repos;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import podcast.models.entities.Series;
import podcast.models.entities.Subscription;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import podcast.models.entities.User;
import rx.Observable;
import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.dsl.Expression.*;
import static podcast.utils.Constants.*;

@Component
public class SubscriptionsRepo {

  private Bucket bucket;
  private Bucket podcastsBucket;

  @Autowired
  public SubscriptionsRepo(@Qualifier("dbBucket") Bucket subscriptionsBucket,
                           @Qualifier("podcastsBucket") Bucket podcastsBucket) {
    this.bucket = subscriptionsBucket;
    this.podcastsBucket = podcastsBucket;
  }

  /** Stores a subscription **/
  public Subscription storeSubscription(Subscription subscription, Series series) {
    series.incrementSubscriberCount();
    bucket.upsert(subscription.toJsonDocument());
    podcastsBucket.upsert(series.toJsonDocument());
    return subscription;
  }

  /** Deletes a subscription **/
  public boolean deleteSubscription(Subscription subscription, Series series) {
    series.decrementSubscriberCount();
    bucket.remove(Subscription.composeKey(subscription));
    podcastsBucket.upsert(series.toJsonDocument());
    return true;
  }

  /** Get a subscription by user and seriesId **/
  public Subscription getSubscription(User user, Long seriesId) {
    JsonDocument doc = bucket.get(Subscription.composeKey(user.getId(), seriesId));
    if (doc == null) {
      return null;
    } else {
      return new Subscription(doc.content());
    }
  }

  /** Get a user's subscriptions **/
  public List<Subscription> getUserSubscriptions(User user) {
    N1qlQuery q = N1qlQuery.simple(
      select("*").from("`"+DB+"`")
        .where(
          (x(TYPE).eq(s(SUBSCRIPTION)))
            .and(x(USER_ID).eq(s(user.getId())))
        )
    );
    List<N1qlQueryRow> rows = bucket.query(q).allRows();
    return rows.stream()
      .map(r -> new Subscription(r.value().getObject(DB))).collect(Collectors.toList());
  }
}
