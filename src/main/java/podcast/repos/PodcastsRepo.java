package podcast.repos;

import com.couchbase.client.java.Bucket;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import podcast.models.entities.Episode;
import podcast.models.entities.Podcast;
import podcast.models.entities.Series;

@Component
public class PodcastsRepo {

  /* Connection to DB */
  private Bucket bucket;

  public PodcastsRepo(@Qualifier("podcastsBucket") Bucket podcastsBucket) {
    this.bucket = podcastsBucket;
  }


  /** Get episode by seriesId and timestamp **/
  public Episode getEpisodeBySeriesIdAndTimestamp(Long seriesId, Long timestamp) {
    return new Episode(bucket.get(Podcast.composeKey(seriesId, timestamp)).content());
  }


  /** Get series by id **/
  public Series getSeries(Long seriesId) {
    // TODO
    return null;
  }


}
