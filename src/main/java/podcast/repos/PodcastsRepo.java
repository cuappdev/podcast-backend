package podcast.repos;

import com.couchbase.client.java.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class PodcastsRepo {

  /* Connection to DB */
  private Bucket bucket;

  @Autowired
  public PodcastsRepo(@Qualifier("podcastsBucket") Bucket podcastsBucket) {
    this.bucket = podcastsBucket;
  }

  // TODO - functions that deal with single lookups + such

}
