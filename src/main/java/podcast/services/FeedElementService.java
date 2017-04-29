package podcast.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import podcast.models.entities.feeds.FeedElement;
import podcast.repos.FeedElementRepo;

import java.util.List;

@Service
public class FeedElementService {

  private final FeedElementRepo feedElementRepo;

  @Autowired
  public FeedElementService(FeedElementRepo feedElementRepo) {
    this.feedElementRepo = feedElementRepo;
  }

  public List<FeedElement> getFeedElements(String userId) {
    // TODO
    return null;
  }

  // MARK - listeners

  @EventListener
  private void handleRecommendationCreation(RecommendationsService.RecommendationCreationEvent creationEvent) {
    feedElementRepo.handleRecommendationCreation(creationEvent.episode.getId(), creationEvent.user.getId());
  }

  @EventListener
  private void handleRecommendationDeletion(RecommendationsService.RecommendationDeletionEvent deletionEvent) {
    feedElementRepo.handleRecommendationDeletion(deletionEvent.episode.getId(), deletionEvent.user.getId());
  }

}
