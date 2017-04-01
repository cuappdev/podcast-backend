package podcast.search;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import podcast.models.entities.*;
import podcast.services.SubscriptionsService;
import podcast.utils.Constants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import static podcast.utils.Constants.*;

/** Podcasts search via indexes on certain fields **/
@Component
@Qualifier("simplePodcastsSearch")
public class SimplePodcastsSearch extends PodcastsSearch {

  private Bucket bucket;

  @Autowired
  public SimplePodcastsSearch(@Qualifier("podcastsBucket") Bucket podcastsBucket,
                              SubscriptionsService subscriptionsService) {
    super(subscriptionsService);
    this.bucket = podcastsBucket;
  }

  /** {@link PodcastsSearch#searchEpisodes(String, Integer, Integer, User)} **/
  public List<Episode> searchEpisodes(String query, Integer offset, Integer max, User user) throws Exception {
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

  /** {@link PodcastsSearch#searchSeries(String, Integer, Integer, User)} **/
  public List<Series> searchSeries(String query, Integer offset, Integer max, User user) throws Exception {
    query = query.trim(); // cleanse the query
    String qS = "SELECT * FROM `%s` WHERE %s='%s' AND %s LIKE '%s%%' OFFSET %d LIMIT %d";
    String queryString = String.format(qS, PODCASTS, TYPE, SERIES, TITLE, query, offset, max);
    N1qlQuery q = N1qlQuery.simple(queryString);
    List<N1qlQueryRow> rows = bucket.query(q).allRows();

    return addSubscribed(rows.stream()
      .map(r -> { return new Series(r.value().getObject(PODCASTS)); })
      .collect(Collectors.toList()), user);
  }


  /** {@link PodcastsSearch#searchEverything(String, Integer, Integer, User)} **/
  public List<Podcast> searchEverything(String query, Integer offset, Integer max, User user) throws Exception {
    query = query.trim(); // cleanse the query
    String qS = "SELECT * FROM `%s` WHERE %s LIKE '%s%%' OR %s LIKE '%s%%' OFFSET %d LIMIT %d";
    String queryString = String.format(qS, PODCASTS, TITLE, query, SERIES_TITLE, query, offset, max);
    N1qlQuery q = N1qlQuery.simple(queryString);
    List<N1qlQueryRow> rows = bucket.query(q).allRows();

    List<Series> series = addSubscribed(rows.stream()
      .filter(r -> {
        JsonObject object = r.value().getObject(PODCASTS);
        return object.getString(TYPE).equals(SERIES);
      }).map(s -> (Series) s).collect(Collectors.toList()), user);
    List<Episode> episodes = rows.stream()
      .filter(r -> {
        JsonObject object = r.value().getObject(PODCASTS);
        return object.getString(TYPE).equals(EPISODE);
      }).map(e -> (Episode) e).collect(Collectors.toList());
    List<Podcast> result = new ArrayList<Podcast>();
    result.addAll(series);
    result.addAll(episodes);
    return result;
  }


}
