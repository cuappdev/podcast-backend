package podcast.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import podcast.models.entities.history.ListeningHistory;
import podcast.models.entities.podcasts.Episode;
import podcast.repos.ListeningHistoryRepo;
import podcast.repos.PodcastsRepo;
import java.util.List;

@Service
public class ListeningHistoryService {

  private final ListeningHistoryRepo listeningHistoryRepo;
  private final PodcastsRepo podcastsRepo;

  @Autowired
  public ListeningHistoryService(ListeningHistoryRepo listeningHistoryRepo,
                                 PodcastsRepo podcastsRepo) {
    this.listeningHistoryRepo = listeningHistoryRepo;
    this.podcastsRepo = podcastsRepo;
  }

  /** Create listening history **/
  public ListeningHistory createListeningHistory(String userId, String episodeId) throws Exception {
    Episode episode = podcastsRepo.getEpisodeById(episodeId);
    ListeningHistory listeningHistory = new ListeningHistory(episode, userId);
    return listeningHistoryRepo.storeListeningHistory(listeningHistory);
  }

  /** Delete listening history **/
  public ListeningHistory deleteListeningHistory(String userId, String episodeId) throws Exception {
    ListeningHistory listeningHistory = listeningHistoryRepo.getListeningHistory(userId, episodeId);
    return listeningHistoryRepo.deleteListeningHistory(listeningHistory);
  }

  /** Clear listening history of a particular user **/
  public void clearListeningHistory(String userId) {
    listeningHistoryRepo.removeAllListeningHistories(userId);
  }

  /** Get a user's listening history (paginated) **/
  public List<ListeningHistory> getUserListeningHistory(String userId, Integer offset, Integer max) {
    return listeningHistoryRepo.getUserListeningHistories(userId, offset, max);
  }

}
