package podcast.repos;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.couchbase.client.java.query.dsl.Sort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import podcast.models.entities.feeds.FeedElement;
import podcast.models.entities.feeds.RecommendationFeedElement;
import podcast.models.entities.podcasts.Episode;
import podcast.models.entities.users.User;
import java.util.List;
import java.util.stream.Collectors;
import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.dsl.Expression.*;
import static podcast.utils.Constants.*;
import static podcast.utils.Lambdas.retry;

@Component
public class FeedElementRepo {

  private final Bucket bucket;

  public FeedElementRepo(@Qualifier("dbBucket") Bucket bucket) {
    this.bucket = bucket;
  }

  /** Get someone's feed **/
  public List<FeedElement> getFeed(String ownerId, Integer offset, Integer max) {
    N1qlQuery q = N1qlQuery.simple(
      select("*").from("`" + DB + "`")
      .where(
        (x(TYPE).eq(s(FEED)))
        .and(x(OWNER_ID).eq(s(ownerId)))
      ).orderBy(Sort.desc(UPDATED_AT))
      .limit(max)
      .offset(offset)
    );
    System.out.println(q);
    List<N1qlQueryRow> rows = bucket.query(q).allRows();
    return rows.stream()
      .map(r -> {
        JsonObject object = r.value().getObject(DB);
        FeedElement.FeedType type = FeedElement.FeedType.valueOf(object.getString(FEED_TYPE));
        switch(type) {
          case recommendationFeedElement: {
            return new RecommendationFeedElement(object);
          }
          case releaseFeedElement:
            return null;
          default:
            return null;
        }
      }).collect(Collectors.toList());
  }

  /** Handle recommendation creation event -
   *  Create a new RecommendationFeedElement or add recommender to the current element **/
  public void handleRecommendationCreation(Episode episode,
                                           User recommender,
                                           List<String> followerIds) {
    List<RecommendationFeedElement> feedElements = followerIds
      .stream()
      .map(fId -> new RecommendationFeedElement(fId, episode, recommender))
      .collect(Collectors.toList());
    for (RecommendationFeedElement feedElement : feedElements) {
      bucket.upsert(feedElement.toJsonDocument());
    }
  }

  /** Handle recommendation deletion event -
   * Delete the RecommendationFeedElement or remove the recommender from the current element **/
  public void handleRecommendationDeletion(Episode episode,
                                           User recommender,
                                           List<String> followerIds) {
    List<RecommendationFeedElement> feedElements = followerIds
      .stream()
      .map(fId -> new RecommendationFeedElement(fId, episode, recommender))
      .collect(Collectors.toList());
    for (RecommendationFeedElement feedElement : feedElements) {
      bucket.remove(RecommendationFeedElement.composeKey(feedElement));
    }
  }
}
