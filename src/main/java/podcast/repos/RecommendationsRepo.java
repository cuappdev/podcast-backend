package podcast.repos;

import com.couchbase.client.java.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import podcast.models.entities.Episode;
import podcast.models.entities.Recommendation;

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

}
