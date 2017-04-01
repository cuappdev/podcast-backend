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
import java.util.ArrayList;
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
    List<JsonDocument> docs = Arrays.asList(
      subscription.toJsonDocument(),
      series.toJsonDocument()
    );
    Observable
        .from(docs)
        .flatMap(doc -> {
            return bucket.async().upsert(doc);
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
      Series.composeKey(series.getId(), SERIES_PUB_DATE)
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
    N1qlQuery q = N1qlQuery.simple(
        select("*").from("`"+DB+"`")
        .where(
            (x(TYPE).eq(s(SUBSCRIPTION)))
            .and(x(USER_ID).eq(s(user.getId()))
            )
        )
    );
    List<N1qlQueryRow> rows = bucket.query(q).allRows();
    return rows.stream()
        .map(r -> new Subscription(r.value().getObject(DB))).collect(Collectors.toList());
  }
}
