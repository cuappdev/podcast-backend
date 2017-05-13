package podcast.services;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import podcast.models.entities.podcasts.Episode;
import podcast.models.entities.podcasts.Series;
import podcast.models.entities.podcasts.EpisodeStat;
import podcast.models.entities.podcasts.SeriesStat;
import podcast.repos.PodcastsRepo;
import podcast.repos.RecommendationsRepo;
import podcast.repos.SubscriptionsRepo;
import java.util.HashMap;
import java.util.List;

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
  public Episode getEpisode(String episodeId) throws Exception {
    try {
      return podcastsRepo.getEpisodeById(episodeId);
    } catch (Exception e) {
      throw new Episode.EpisodeDoesNotExistException();
    }
  }

  /** Fetch a series given its seriesId **/
  public Series getSeries(Long seriesId) throws Exception {
    try {
      return podcastsRepo.getSeries(seriesId);
    } catch (Exception e) {
      throw new Series.SeriesDoesNotExistException();
    }
  }

  /** Paginated episodes by seriesId **/
  public List<Episode> getEpisodesBySeriesId(Long seriesId,
                                             Integer offset,
                                             Integer max) throws Exception {
    return podcastsRepo.getEpisodesBySeriesId(seriesId, offset, max);
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

  // MARK - Info Wrappers

  public class EpisodesInfo {
    @Getter private HashMap<String, EpisodeStat> episodeStats;
    @Getter private HashMap<String, Boolean> recommendations;

    private EpisodesInfo(String userId, List<String> episodeIds) {
      this.episodeStats = podcastsRepo.episodeStats(episodeIds);
      this.recommendations = recommendationsRepo.getEpsiodeRecommendationMappings(userId, episodeIds);
    }
  }

  public class SeriesInfo {
    @Getter private HashMap<Long, SeriesStat> seriesStats;
    @Getter private HashMap<Long, Boolean> subscriptions;

    private SeriesInfo(String userId, List<Long> seriesIds) {
      this.seriesStats = podcastsRepo.seriesStats(seriesIds);
      this.subscriptions = subscriptionsRepo.getSeriesSubscriptionMappings(userId, seriesIds);
    }
  }

}
