package podcast.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import podcast.models.entities.Episode;
import podcast.repos.PodcastsRepo;

/**
 * Service to handle querying podcast
 * data (series, episodes) in Couchbase
 */
@Service
public class PodcastsService {

  /* Database communication */
  private PodcastsRepo podcastsRepo;

  @Autowired
  public PodcastsService(PodcastsRepo podcastsRepo) {
    this.podcastsRepo = podcastsRepo;
  }

  /** Fetch a episode given its seriesId and timestamp **/
  public Episode getEpisode(Long seriesId, Long timestamp) throws Exception {
    try {
      return podcastsRepo.getEpisodeBySeriesIdAndTimestamp(seriesId, timestamp);
    } catch (Exception e) {
      throw new EpisodeDoesNotExistException();
    }
  }

  /** When an episode does not exist **/
  public class EpisodeDoesNotExistException extends Exception {
    public EpisodeDoesNotExistException() {
      super("Episode does not exist");
    }
  }

}