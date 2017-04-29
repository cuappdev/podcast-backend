package podcast.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import podcast.models.entities.podcasts.Episode;
import podcast.models.entities.podcasts.Series;
import podcast.models.entities.subscriptions.Subscription;
import podcast.models.entities.users.User;
import podcast.repos.PodcastsRepo;
import podcast.repos.SubscriptionsRepo;
import rx.Observable;
import java.util.List;
import static podcast.utils.Lambdas.*;

/**
 * Service to handle querying podcast
 * data (series, episodes) in Couchbase
 */
@Service
public class PodcastsService {

  private final PodcastsRepo podcastsRepo;
  private final SubscriptionsRepo subscriptionsRepo;

  @Autowired
  public PodcastsService(PodcastsRepo podcastsRepo,
                         SubscriptionsRepo subscriptionsRepo) {
    this.podcastsRepo = podcastsRepo;
    this.subscriptionsRepo = subscriptionsRepo;
  }

  /** Fetch a episode given its seriesId and timestamp **/
  public Episode getEpisode(Long seriesId, Long pubDate) throws Exception {
    try {
      return podcastsRepo.getEpisodeBySeriesIdAndPubDate(seriesId, pubDate);
    } catch (Exception e) {
      throw new Episode.EpisodeDoesNotExistException();
    }
  }

  /** Getch a series given its seriesId **/
  public Series getSeries(User loggedInUser, Long seriesId) throws Exception {
    try {
      Series series = podcastsRepo.getSeries(seriesId);
      Subscription sub = subscriptionsRepo.getSubscription(loggedInUser, seriesId);
      return series;
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

}
