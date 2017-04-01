package podcast.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import podcast.models.entities.Episode;
import podcast.models.entities.Series;
import podcast.models.entities.Subscription;
import podcast.models.entities.User;
import podcast.repos.PodcastsRepo;
import podcast.repos.SubscriptionsRepo;
import java.util.List;

/**
 * Service to handle querying podcast
 * data (series, episodes) in Couchbase
 */
@Service
public class PodcastsService {

  /* Database communication */
  private PodcastsRepo podcastsRepo;
  private SubscriptionsRepo subscriptionsRepo;

  @Autowired
  public PodcastsService(PodcastsRepo podcastsRepo,
                         SubscriptionsRepo subscriptionsRepo) {
    this.podcastsRepo = podcastsRepo;
    this.subscriptionsRepo = subscriptionsRepo;
  }


  /** Fetch a episode given its seriesId and timestamp **/
  public Episode getEpisode(Long seriesId, Long timestamp) throws Exception {
    try {
      return podcastsRepo.getEpisodeBySeriesIdAndTimestamp(seriesId, timestamp);
    } catch (Exception e) {
      throw new Episode.EpisodeDoesNotExistException();
    }
  }

  /** Getch a series given its seriesId **/
  public Series getSeries(User loggedInUser, Long seriesId) throws Exception {
    try {
      Series series = podcastsRepo.getSeries(seriesId);
      Subscription sub = subscriptionsRepo.getSubscription(loggedInUser, seriesId);
      series.setIsSubscribed(sub != null);
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

}
