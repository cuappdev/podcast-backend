package podcast.repos;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.couchbase.client.java.query.dsl.Sort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import podcast.models.entities.Episode;
import podcast.models.entities.Podcast;
import podcast.models.entities.Series;
import java.util.stream.Collectors;
import java.util.List;
import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.dsl.Expression.*;
import static podcast.utils.Constants.*;

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
    return new Series(bucket.get(Podcast.composeKey(seriesId, 9223372036854775807L)).content());
  }


  /** Get episodes by seriesId (paginated) **/
  public List<Episode> getEpisodesBySeriesId(Long seriesId,
                                             Integer offset,
                                             Integer max) throws Exception {
    N1qlQuery q = N1qlQuery.simple(
      select("*").from("`" + PODCASTS + "`")
      .where(
        (x(TYPE).eq(s(EPISODE)))
        .and(x(SERIES_ID).eq(x(seriesId)))
      )
      .orderBy(Sort.desc(PUB_DATE))
      .limit(max)
      .offset(offset)
    );

    List<N1qlQueryRow> rows = bucket.query(q).allRows();
    return rows.stream()
      .map(r -> new Episode(r.value().getObject(PODCASTS))).collect(Collectors.toList());
  }

}
