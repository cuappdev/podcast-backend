package podcast.repos;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import podcast.models.entities.Episode;
import podcast.models.entities.Recommendation;
import podcast.models.entities.User;
import java.util.List;
import java.util.stream.Collectors;
import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.dsl.Expression.*;
import static podcast.utils.Constants.*;

@Component
public class RecommendationsRepo {

  private Bucket bucket;
  private Bucket podcastsBucket;

  @Autowired
  public RecommendationsRepo(@Qualifier("dbBucket") Bucket bucket,
                             @Qualifier("podcastsBucket") Bucket podcastsBucket) {
    this.bucket = bucket;
    this.podcastsBucket = podcastsBucket;
  }

  /** Stores a recommendation */
  public Recommendation storeRecommendation(Recommendation recommendation,
                                            Episode episode) {
    episode.incrementNumberRecommenders();
    bucket.upsert(recommendation.toJsonDocument());
    podcastsBucket.upsert(episode.toJsonDocument());
    return recommendation;
  }

  /** Deletes a recommendation */
  public boolean deleteRecommendation(Recommendation recommendation,
                                      Episode episode) {
    if (recommendation == null) {
      return false;
    }
    episode.decrementNumberRecommenders();
    bucket.remove(Recommendation.composeKey(recommendation));
    podcastsBucket.upsert(episode.toJsonDocument());
    return true;
  }

  /** Get a recommendation by user and episodeId */
  public Recommendation getRecommendation(User user, String episodeId) {
    JsonDocument doc = bucket.get(Recommendation.composeKey(episodeId, user.getId()));
    if (doc == null) {
      return null;
    } else {
      return new Recommendation(doc.content());
    }
  }

  /** Get a user's recommendations */
  public List<Recommendation> getUserRecommendations(User user) {
    N1qlQuery q = N1qlQuery.simple(
      select("*").from("`" + DB + "`")
        .where(
          (x(TYPE).eq(s(RECOMMENDATION)))
          .and(x("`" + USER + "`.`" + ID + "`").eq(s(user.getId())))
        )
    );
    List<N1qlQueryRow> rows = bucket.query(q).allRows();
    return rows.stream()
      .map(r -> new Recommendation(r.value().getObject(DB))).collect(Collectors.toList());
  }

}
