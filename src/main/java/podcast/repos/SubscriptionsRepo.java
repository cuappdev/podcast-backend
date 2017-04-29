package podcast.repos;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import podcast.models.entities.subscriptions.Subscription;
import java.util.List;
import java.util.stream.Collectors;
import podcast.models.entities.users.User;
import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.dsl.Expression.*;
import static podcast.utils.Constants.*;

@Component
public class SubscriptionsRepo {

  private Bucket bucket;

  @Autowired
  public SubscriptionsRepo(@Qualifier("dbBucket") Bucket subscriptionsBucket) {
    this.bucket = subscriptionsBucket;
  }

  /** Stores a subscription **/
  public Subscription storeSubscription(Subscription subscription) {
    bucket.upsert(subscription.toJsonDocument());
    return subscription;
  }

  /** Deletes a subscription **/
  public Subscription deleteSubscription(Subscription subscription) {
    if (subscription == null) return null;
    bucket.remove(Subscription.composeKey(subscription));
    return subscription;
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

  /** Get the subscriptions of a particular series **/
  public List<Subscription> getSubscriptions(Long seriesId, Integer offset, Integer max) {
    N1qlQuery q = N1qlQuery.simple(
      select("*").from("`" + DB + "`")
      .where(
        (x(TYPE).eq(s(SUBSCRIPTION)))
        .and(x("`" + SERIES_ID + "`").eq(x(seriesId)))
      ).limit(max).offset(offset)
    );
    List<N1qlQueryRow> rows = bucket.query(q).allRows();
    return rows.stream()
      .map(r -> new Subscription(r.value().getObject(DB))).collect(Collectors.toList());
  }

  /** Get a user's subscriptions **/
  public List<Subscription> getUserSubscriptions(User user) {
    N1qlQuery q = N1qlQuery.simple(
      select("*").from("`" + DB + "`")
        .where(
          (x(TYPE).eq(s(SUBSCRIPTION)))
            .and(x("`" + USER + "`.`" + ID + "`").eq(s(user.getId())))
        )
    );
    List<N1qlQueryRow> rows = bucket.query(q).allRows();
    return rows.stream()
      .map(r -> new Subscription(r.value().getObject(DB))).collect(Collectors.toList());
  }
}
