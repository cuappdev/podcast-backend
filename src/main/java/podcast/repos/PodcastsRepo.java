package podcast.repos;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.couchbase.client.java.query.dsl.Sort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import podcast.models.entities.podcasts.Episode;
import podcast.models.entities.podcasts.Series;
import podcast.models.entities.podcasts.EpisodeStat;
import podcast.models.entities.podcasts.SeriesStat;
import rx.Observable;

import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.List;
import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.dsl.Expression.*;
import static podcast.utils.Constants.*;
import static podcast.utils.Lambdas.retry;

@Component
public class PodcastsRepo {

  private Bucket bucket;
  private Bucket dbBucket;
  public PodcastsRepo(@Qualifier("podcastsBucket") Bucket podcastsBucket,
                      @Qualifier("dbBucket") Bucket dbBucket) {
    this.bucket = podcastsBucket;
    this.dbBucket = dbBucket;
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
    String key = EpisodeStat.composeKey(episodeId);
    Observable.defer(() -> {
      try {
        return Observable.just(bucket.get(key));
      } catch (Exception e) {
        return Observable.just(null);
      }
    }).map(EpisodeStat::incrementNumberRecommenders)
      .flatMap(doc -> {
        if (doc != null) {
          return Observable.just(bucket.replace(doc));
        } else {
          EpisodeStat episodeStats = new EpisodeStat(episodeId);
          JsonDocument newDoc = EpisodeStat.incrementNumberRecommenders(episodeStats.toJsonDocument());
          return Observable.just(bucket.upsert(newDoc));
        }
      })
      .retryWhen(attempts -> retry.operation(attempts))
      .subscribe();
  }

  /** Decrement episode recommendations **/
  public void decrementEpisodeRecommendations(String episodeId) {
    String key = EpisodeStat.composeKey(episodeId);
    Observable.defer(() -> {
      try {
        return Observable.just(bucket.get(key));
      } catch (Exception e) {
        return Observable.just(null);
      }
    }).map(EpisodeStat::decrementNumberRecommenders)
      .flatMap(doc -> Observable.just(bucket.replace(doc)))
      .retryWhen(attempts -> retry.operation(attempts))
      .subscribe();
  }

  /** Increment series subscribers **/
  public void incrementSeriesSubscribers(Long seriesId) {
    String key = SeriesStat.composeKey(seriesId);
    Observable.defer(() -> {
      try {
        return Observable.just(bucket.get(key));
      } catch (Exception e) {
        return Observable.just(null);
      }
    }).map(SeriesStat::incrementSubscriberCount)
      .flatMap(doc -> {
        if (doc != null) {
          return Observable.just(bucket.replace(doc));
        } else {
          SeriesStat seriesStats = new SeriesStat(seriesId);
          JsonDocument newDoc = SeriesStat.incrementSubscriberCount(seriesStats.toJsonDocument());
          return Observable.just(bucket.upsert(newDoc));
        }
      })
      .retryWhen(attempts -> retry.operation(attempts))
      .subscribe();
  }

  /** Decrement series subscribers **/
  public void decrementSeriesSubscribers(Long seriesId) {
    String key = SeriesStat.composeKey(seriesId);
    Observable.defer(() -> {
      try {
        return Observable.just(bucket.get(key));
      } catch (Exception e) {
        return Observable.just(null);
      }
    }).map(SeriesStat::decrementSubscriberCount)
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

  /** Get seriesId -> series stat mappings */
  public HashMap<Long, SeriesStat> seriesStats(List<Long> seriesIds) {
    List<String> keys = seriesIds.stream().map(id -> SeriesStat.composeKey(id)).collect(Collectors.toList());
    List<JsonDocument> foundDocs = Observable.from(keys)
      .flatMap(key -> Observable.just(dbBucket.get(key)))
      .toList()
      .toBlocking()
      .single();
    List<SeriesStat> seriesStats = foundDocs.stream()
      .filter(doc -> doc != null)
      .map(doc -> new SeriesStat(doc.content()))
      .collect(Collectors.toList());
    HashMap<Long, SeriesStat> result = new HashMap<>();
    for (SeriesStat seriesStat : seriesStats) {
      result.put(seriesStat.getSeriesId(), seriesStat);
    }
    for (Long seriesId : seriesIds) {
      if (!result.containsKey(seriesId)) result.put(seriesId, new SeriesStat(seriesId));
    }
    return result;
  }

  /** Get episodeId -> episode stat mappings */
  public HashMap<String, EpisodeStat> episodeStats(List<String> episodeIds) {
    List<String> keys = episodeIds.stream().map(id -> EpisodeStat.composeKey(id)).collect(Collectors.toList());
    List<JsonDocument> foundDocs = Observable.from(keys)
      .flatMap(key -> Observable.just(dbBucket.get(key)))
      .toList()
      .toBlocking()
      .single();
    List<EpisodeStat> episodeStats = foundDocs.stream()
      .filter(doc -> doc != null)
      .map(doc -> new EpisodeStat(doc.content()))
      .collect(Collectors.toList());
    HashMap<String, EpisodeStat> result = new HashMap<>();
    for (EpisodeStat episodeStat : episodeStats) {
      result.put(episodeStat.getEpisodeId(),  episodeStat);
    }
    for (String episodeId : episodeIds) {
      if (!result.containsKey(episodeId)) result.put(episodeId, new EpisodeStat(episodeId));
    }
    return result;
  }

}
