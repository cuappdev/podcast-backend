package podcast.search;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryRow;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import podcast.models.entities.Episode;
import podcast.models.entities.Podcast;
import podcast.models.entities.Series;
import podcast.utils.Constants;
import java.util.List;
import java.util.stream.Collectors;
import static podcast.utils.Constants.*;

/** Podcasts search via indexes on certain fields **/
@Component
@Qualifier("simplePodcastsSearch")
public class SimplePodcastsSearch extends PodcastsSearch {

  private Bucket bucket;

  public SimplePodcastsSearch(@Qualifier("podcastsBucket") Bucket podcastsBucket) {
    this.bucket = podcastsBucket;
  }

  /** {@link PodcastsSearch#searchEpisodes(String, Integer, Integer)} **/
  public List<Episode> searchEpisodes(String query, Integer offset, Integer max) {
    query = query.trim(); // cleanse the query
    String qS = "SELECT * FROM `%s` WHERE %s='%s' AND %s LIKE '%s%%' OFFSET %d LIMIT %d";
    String queryString = String.format(qS, PODCASTS, TYPE, EPISODE, TITLE, query, offset, max);
    N1qlQuery q = N1qlQuery.simple(queryString);
    List<N1qlQueryRow> rows = bucket.query(q).allRows();

    return rows.stream()
      .map(r -> {
        return new Episode(r.value().getObject(PODCASTS)); })
      .collect(Collectors.toList());
  }


  /** {@link PodcastsSearch#searchSeries(String, Integer, Integer)} **/
  public List<Series> searchSeries(String query, Integer offset, Integer max) {
    query = query.trim(); // cleanse the query
    String qS = "SELECT * FROM `%s` WHERE %s='%s' AND %s LIKE '%s%%' OFFSET %d LIMIT %d";
    String queryString = String.format(qS, PODCASTS, TYPE, SERIES, TITLE, query, offset, max);
    N1qlQuery q = N1qlQuery.simple(queryString);
    List<N1qlQueryRow> rows = bucket.query(q).allRows();

    return rows.stream()
      .map(r -> { return new Series(r.value().getObject(PODCASTS)); })
      .collect(Collectors.toList());
  }


  /** {@link PodcastsSearch#searchEverything(String, Integer, Integer)} **/
  public List<Podcast> searchEverything(String query, Integer offset, Integer max) {
    query = query.trim(); // cleanse the query
    String qS = "SELECT * FROM `%s` WHERE %s LIKE '%s%%' OR %s LIKE '%s%%' OFFSET %d LIMIT %d";
    String queryString = String.format(qS, PODCASTS, TITLE, query, SERIES_TITLE, query, offset, max);
    N1qlQuery q = N1qlQuery.simple(queryString);
    List<N1qlQueryRow> rows = bucket.query(q).allRows();

    return rows.stream()
      .map(r -> {
        JsonObject object = r.value().getObject(PODCASTS);
        if (object.getString(Constants.TYPE).equals(EPISODE)) {
          return (Podcast) new Episode(object);
        } else {
          return (Podcast) new Series(object);
        }
      }).collect(Collectors.toList());
  }


}
