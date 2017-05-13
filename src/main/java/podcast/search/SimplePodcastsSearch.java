package podcast.search;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import podcast.models.entities.podcasts.Episode;
import podcast.models.entities.podcasts.Series;
import podcast.services.PodcastsService;
import java.util.List;
import java.util.stream.Collectors;
import static podcast.utils.Constants.*;

/** Podcasts search via indexes on certain fields **/
@Component
@Qualifier("simplePodcastsSearch")
public class SimplePodcastsSearch extends PodcastsSearch {

  private Bucket bucket;
  private PodcastsService podcastsService;

  @Autowired
  public SimplePodcastsSearch(@Qualifier("podcastsBucket") Bucket podcastsBucket,
                              PodcastsService podcastsService) {
    this.podcastsService = podcastsService;
    this.bucket = podcastsBucket;
  }

  /** {@link PodcastsSearch#searchEpisodes(String, Integer, Integer)} **/
  public List<Episode> searchEpisodes(String query, Integer offset, Integer max) throws Exception {
    query = query.trim(); // cleanse the query
    String qS = "SELECT * FROM `%s` WHERE %s='%s' AND %s LIKE '%s%%' OFFSET %d LIMIT %d";
    String queryString = String.format(qS, PODCASTS, TYPE, EPISODE, TITLE, query, offset, max);
    N1qlQuery q = N1qlQuery.simple(queryString);
    List<N1qlQueryRow> rows = bucket.query(q).allRows();
    return rows.stream()
      .map(r -> new Episode(r.value().getObject(PODCASTS)))
      .collect(Collectors.toList());
  }

  /** {@link PodcastsSearch#searchSeries(String, Integer, Integer)} **/
  public List<Series> searchSeries(String query, Integer offset, Integer max) throws Exception {
    query = query.trim(); // cleanse the query
    String qS = "SELECT * FROM `%s` WHERE %s='%s' AND %s LIKE '%s%%' OFFSET %d LIMIT %d";
    String queryString = String.format(qS, PODCASTS, TYPE, SERIES, TITLE, query, offset, max);
    N1qlQuery q = N1qlQuery.simple(queryString);
    List<N1qlQueryRow> rows = bucket.query(q).allRows();
    return rows.stream()
      .map(r -> new Series(r.value().getObject(PODCASTS)))
      .collect(Collectors.toList());
  }

}
