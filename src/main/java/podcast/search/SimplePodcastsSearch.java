package podcast.search;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryRow;
import org.springframework.beans.factory.annotation.Qualifier;
import podcast.models.entities.Episode;
import podcast.models.entities.Podcast;
import podcast.models.entities.Series;
import podcast.models.utils.Constants;
import java.util.List;
import java.util.stream.Collectors;

/** Podcasts search via indexes on certain fields **/
public class SimplePodcastsSearch extends PodcastsSearch {

  private Bucket bucket;

  SimplePodcastsSearch(@Qualifier("podcastsBucket") Bucket podcastsBucket) {
    this.bucket = bucket;
  }

  /** {@link PodcastsSearch#searchEpisodes(String)} **/
  public List<Episode> searchEpisodes(String query) {
    query = query.trim(); // cleanse the query
    String queryString =
      "SELECT * FROM podcasts WHERE " +
        "type='episode' AND seriesTitle LIKE '" + query + "%' " +
          "OR title LIKE '" + query + "%'";
    N1qlQuery q = N1qlQuery.simple(queryString);
    List<N1qlQueryRow> rows = bucket.query(q).allRows();

    return rows.stream()
      .map(r -> { return new Episode(r.value()); })
      .collect(Collectors.toList());
  }


  /** {@link PodcastsSearch#searchSeries(String)} **/
  public List<Series> searchSeries(String query) {
    query = query.trim(); // cleanse the query
    String queryString =
      "SELECT * FROM podasts WHERE " +
        "type='series' AND title LIKE '" + query + "%'";
    N1qlQuery q = N1qlQuery.simple(queryString);
    List<N1qlQueryRow> rows = bucket.query(q).allRows();

    return rows.stream()
      .map(r -> { return new Series(r.value()); })
      .collect(Collectors.toList());
  }


  /** {@link PodcastsSearch#searchEverything(String)} **/
  public List<Podcast> searchEverything(String query) {
    query = query.trim(); // cleanse the query
    String queryString =
      "SELECT * FROM podcasts WHERE " +
        "title LIKE '" + query + "%' " +
        "OR seriesTitle LIKE '" + query + "%'";
    N1qlQuery q = N1qlQuery.simple(queryString);
    List<N1qlQueryRow> rows = bucket.query(q).allRows();

    return rows.stream()
      .map(r -> {
        JsonObject object = r.value();
        if (object.getString(Constants.TYPE).equals(Constants.EPISODE)) {
          return (Podcast) new Episode(object);
        } else {
          return (Podcast) new Series(object);
        }
      }).collect(Collectors.toList());
  }


}
