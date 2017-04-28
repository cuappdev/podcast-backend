package podcast.repos;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.couchbase.client.java.query.dsl.Sort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import podcast.models.entities.podcasts.Episode;
import podcast.models.entities.podcasts.Series;
import rx.Observable;
import java.util.stream.Collectors;
import java.util.List;
import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.dsl.Expression.*;
import static podcast.utils.Constants.*;
import static podcast.utils.Lambdas.retry;

@Component
public class PodcastsRepo {

  private Bucket bucket;

  public PodcastsRepo(@Qualifier("podcastsBucket") Bucket podcastsBucket) {
    this.bucket = podcastsBucket;
  }

  /** Get episode by Id */
  public Episode getEpisodeById(String episodeId) {
    Episode.CompositeEpisodeKey comp = Episode.getSeriesIdAndPubDate(episodeId);
    return getEpisodeBySeriesIdAndPubDate(comp.getSeriesId(), comp.getPubDate());
  }

  /** Get episode by seriesId and timestamp **/
  public Episode getEpisodeBySeriesIdAndPubDate(Long seriesId, Long timestamp) {
    return new Episode(bucket.get(Episode.composeKey(seriesId, timestamp)).content());
  }

  /** Increment episode recommendations **/
  public void incrementEpisodeRecommendations(String episodeId) {
    Episode.CompositeEpisodeKey comp = Episode.getSeriesIdAndPubDate(episodeId);
    String key = Episode.composeKey(comp.getSeriesId(), comp.getPubDate());
    Observable.defer(() -> {
      try {
        return Observable.just(bucket.get(key));
      } catch (Exception e) {
        return Observable.just(null);
      }
    }).map(Episode::incrementNumberRecommenders)
      .flatMap(doc -> Observable.just(bucket.replace(doc)))
      .retryWhen(attempts -> retry.operation(attempts))
      .subscribe();
  }

  /** Decrement episode recommendations **/
  public void decrementEpisodeRecommendations(String episodeId) {
    Episode.CompositeEpisodeKey comp = Episode.getSeriesIdAndPubDate(episodeId);
    String key = Episode.composeKey(comp.getSeriesId(), comp.getPubDate());
    Observable.defer(() -> {
      try {
        return Observable.just(bucket.get(key));
      } catch (Exception e) {
        return Observable.just(null);
      }
    }).map(Episode::decrementNumberRecommenders)
      .flatMap(doc -> Observable.just(bucket.replace(doc)))
      .retryWhen(attempts -> retry.operation(attempts))
      .subscribe();
  }

  /** Increment series subscribers **/
  public void incrementSeriesSubscribers(Long seriesId) {
    String key = Series.composeKey(seriesId);
    Observable.defer(() -> {
      try {
        return Observable.just(bucket.get(key));
      } catch (Exception e) {
        return Observable.just(null);
      }
    }).map(Series::incrementSubscriberCount)
    .flatMap(doc -> Observable.just(bucket.replace(doc)))
      .retryWhen(attempts -> retry.operation(attempts))
      .subscribe();
  }

  /** Decrement series subscribers **/
  public void decrementSeriesSubscribers(Long seriesId) {
    String key = Series.composeKey(seriesId);
    Observable.defer(() -> {
      try {
        return Observable.just(bucket.get(key));
      } catch (Exception e) {
        return Observable.just(null);
      }
    }).map(Series::decrementSubscriberCount)
      .flatMap(doc -> Observable.just(bucket.replace(doc)))
      .retryWhen(attempts -> retry.operation(attempts))
      .subscribe();
  }

  /** Get series by id **/
  public Series getSeries(Long seriesId) {
    return new Series(bucket.get(Series.composeKey(seriesId)).content());
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
