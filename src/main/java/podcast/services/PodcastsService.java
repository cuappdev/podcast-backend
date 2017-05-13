package podcast.services;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import podcast.models.entities.podcasts.Episode;
import podcast.models.entities.podcasts.Series;
import podcast.models.entities.podcasts.EpisodeStat;
import podcast.models.entities.podcasts.SeriesStat;
import podcast.models.entities.subscriptions.Subscription;
import podcast.models.entities.users.User;
import podcast.repos.PodcastsRepo;
import podcast.repos.RecommendationsRepo;
import podcast.repos.SubscriptionsRepo;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service to handle querying podcast
 * data (series, episodes) in Couchbase
 */
@Service
public class PodcastsService {

  private final PodcastsRepo podcastsRepo;
  private final SubscriptionsRepo subscriptionsRepo;
  private final RecommendationsRepo recommendationsRepo;

  @Autowired
  public PodcastsService(PodcastsRepo podcastsRepo,
                         SubscriptionsRepo subscriptionsRepo,
                         RecommendationsRepo recommendationsRepo) {
    this.podcastsRepo = podcastsRepo;
    this.subscriptionsRepo = subscriptionsRepo;
    this.recommendationsRepo = recommendationsRepo;
  }

  /** Fetch a episode given its seriesId and timestamp **/
  public SingleEpisodeInfo getEpisode(String userId, Long seriesId, Long pubDate) throws Exception {
    try {
      return new SingleEpisodeInfo(
        userId,
        podcastsRepo.getEpisodeBySeriesIdAndPubDate(seriesId, pubDate)
      );
    } catch (Exception e) {
      throw new Episode.EpisodeDoesNotExistException();
    }
  }

  /** Fetch a series given its seriesId **/
  public SingleSeriesInfo getSeries(String userId, Long seriesId) throws Exception {
    try {
      Series series = podcastsRepo.getSeries(seriesId);
      return new SingleSeriesInfo(userId, series);
    } catch (Exception e) {
      throw new Series.SeriesDoesNotExistException();
    }
  }

  /** Paginated episodes by seriesId **/
  public EpisodesInfo getEpisodesBySeriesId(String userId,
                                            Long seriesId,
                                            Integer offset,
                                            Integer max) throws Exception {
    List<Episode> episodes = podcastsRepo.getEpisodesBySeriesId(seriesId, offset, max);
    return new EpisodesInfo(userId, episodes);
  }

  /** Convert to seriesInfo */
  public SeriesInfo convertSeriesToSeriesInfo(String userId, List<Series> series) {
    return new SeriesInfo(userId, series);
  }

  /** Convert to episodeInfo */
  public EpisodesInfo convertEpisodesToEpsiodesInfo(String userId, List<Episode> episodes) {
    return new EpisodesInfo(userId, episodes);
  }

  // MARK - listeners

  @EventListener
  private void handleSubscriptionCreation(SubscriptionsService.SubscriptionCreationEvent creationEvent) {
    podcastsRepo.incrementSeriesSubscribers(creationEvent.series.getId());
  }

  @EventListener
  private void handleSubscriptionDeletion(SubscriptionsService.SubscriptionDeletionEvent deletionEvent) {
    podcastsRepo.decrementSeriesSubscribers(deletionEvent.series.getId());
  }

  @EventListener
  private void handleRecommendationCreation(RecommendationsService.RecommendationCreationEvent creationEvent) {
    podcastsRepo.incrementEpisodeRecommendations(creationEvent.episode.getId());
  }

  @EventListener
  private void handleRecommendationDeletion(RecommendationsService.RecommendationDeletionEvent deletionEvent) {
    podcastsRepo.decrementEpisodeRecommendations(deletionEvent.episode.getId());
  }

  // MARK - Wrappers

  public abstract class PodcastsInfo {}

  public class EpisodesInfo extends PodcastsInfo {
    @Getter private List<Episode> episodes;
    @Getter private HashMap<String, EpisodeStat> episodeStats;
    @Getter private HashMap<String, Boolean> recommendations;

    private EpisodesInfo(String userId, List<Episode> episodes) {
      this.episodes = episodes;
      List<String> episodeIds = episodes.stream().map(Episode::getId).collect(Collectors.toList());
      this.episodeStats = podcastsRepo.episodeStats(episodeIds);
      this.recommendations = recommendationsRepo.getEpsiodeRecommendationMappings(userId, episodeIds);
    }
  }

  public class SingleEpisodeInfo {
    @Getter private Episode episode;
    @Getter private HashMap<String, EpisodeStat> episodeStats;
    @Getter private HashMap<String, Boolean> recommendations;

    private SingleEpisodeInfo(String userId, Episode episode) {
      this.episode = episode;
      EpisodesInfo episodesInfo = new EpisodesInfo(userId, Collections.singletonList(episode));
      this.episodeStats = episodesInfo.getEpisodeStats();
      this.recommendations = episodesInfo.getRecommendations();
    }
  }

  public class SeriesInfo extends PodcastsInfo {
    @Getter private List<Series> series;
    @Getter private HashMap<Long, SeriesStat> seriesStats;
    @Getter private HashMap<Long, Boolean> subscriptions;

    private SeriesInfo(String userId, List<Series> series) {
      this.series = series;
      List<Long> seriesIds = series.stream().map(Series::getId).collect(Collectors.toList());
      this.seriesStats = podcastsRepo.seriesStats(seriesIds);
      this.subscriptions = subscriptionsRepo.getSeriesSubscriptionMappings(userId, seriesIds);
    }
  }

  public class SingleSeriesInfo {
    @Getter private Series series;
    @Getter private HashMap<Long, SeriesStat> seriesStats;
    @Getter private HashMap<Long, Boolean> subscriptions;

    private SingleSeriesInfo(String userId, Series series) {
      this.series = series;
      SeriesInfo seriesInfo = new SeriesInfo(userId, Collections.singletonList(series));
      this.seriesStats = seriesInfo.getSeriesStats();
      this.subscriptions = seriesInfo.getSubscriptions();
    }
  }

}
