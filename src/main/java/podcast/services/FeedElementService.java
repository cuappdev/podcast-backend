package podcast.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import podcast.models.entities.feeds.FeedElement;
import podcast.models.entities.followings.Follower;
import podcast.repos.FeedElementRepo;
import podcast.repos.FollowersFollowingsRepo;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedElementService {

  private final FeedElementRepo feedElementRepo;
  private final FollowersFollowingsRepo ffRepo;

  @Autowired
  public FeedElementService(FeedElementRepo feedElementRepo,
                            FollowersFollowingsRepo ffRepo) {

    this.feedElementRepo = feedElementRepo;
    this.ffRepo = ffRepo;
  }

  public List<FeedElement> getFeedElements(String userId) {
    // TODO
    return null;
  }

  // MARK - listeners

  @EventListener
  private void handleRecommendationCreation(RecommendationsService.RecommendationCreationEvent creationEvent) throws Exception {
    List<String> followers = ffRepo.getUserFollowers(creationEvent.user.getId()).stream()
      .map(Follower::getId).collect(Collectors.toList());
    feedElementRepo.handleRecommendationCreation(
      creationEvent.episode.getId(),
      creationEvent.user.getId(),
      followers);
  }

  @EventListener
  private void handleRecommendationDeletion(RecommendationsService.RecommendationDeletionEvent deletionEvent) throws Exception {
    List<String> followers = ffRepo.getUserFollowers(deletionEvent.user.getId()).stream()
      .map(Follower::getId).collect(Collectors.toList());
    feedElementRepo.handleRecommendationDeletion(
      deletionEvent.episode.getId(),
      deletionEvent.user.getId(),
      followers);
  }

}
