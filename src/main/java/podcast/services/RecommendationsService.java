package podcast.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import podcast.models.entities.podcasts.Episode;
import podcast.models.entities.recommendations.Recommendation;
import podcast.models.entities.users.User;
import podcast.repos.PodcastsRepo;
import podcast.repos.RecommendationsRepo;
import podcast.repos.UsersRepo;
import java.util.List;

@Service
public class RecommendationsService {

  private final ApplicationEventPublisher publisher;
  private final PodcastsRepo podcastsRepo;
  private final UsersRepo usersRepo;
  private final RecommendationsRepo recommendationsRepo;

  @Autowired
  public RecommendationsService(ApplicationEventPublisher publisher,
                                PodcastsRepo podcastsRepo,
                                UsersRepo usersRepo,
                                RecommendationsRepo recommendationsRepo) {
    this.publisher = publisher;
    this.podcastsRepo = podcastsRepo;
    this.usersRepo = usersRepo;
    this.recommendationsRepo = recommendationsRepo;
  }

  /** Create a recommendation and broadcast the creation event */
  public Recommendation createRecommendation(User owner, String episodeId) {
    Episode episode = podcastsRepo.getEpisodeById(episodeId);
    Recommendation recommendation = new Recommendation(owner, episode);
    publisher.publishEvent(new RecommendationCreationEvent(recommendation, episode, owner));
    return recommendationsRepo.storeRecommendation(recommendation, episode);
  }

  /** Delete a recommendation and broadcast the deletion event */
  public Recommendation deleteRecommendation(User owner, String episodeId) {
    Episode episode = podcastsRepo.getEpisodeById(episodeId);
    Recommendation recommendation = recommendationsRepo.getRecommendation(owner, episodeId);
    publisher.publishEvent(new RecommendationDeletionEvent(recommendation, episode, owner));
    return recommendationsRepo.deleteRecommendation(recommendation, episode);
  }

  /** Get a user's recommendations by the user's ID */
  public List<Recommendation> getUserRecommendations(String userId) throws Exception {
    User user = usersRepo.getUserById(userId);
    return getUserRecommendations(user);
  }

  /** Get a user's recommendations by the user */
  public List<Recommendation> getUserRecommendations(User user) throws Exception {
    return recommendationsRepo.getUserRecommendations(user);
  }

  // MARK - events

  private static abstract class RecommendationEvent {
    Recommendation recommendation;
    Episode episode;
    User user;

    protected RecommendationEvent(Recommendation recommendation,
                                  Episode episode,
                                  User user) {
      this.recommendation = recommendation;
      this.episode = episode;
      this.user = user;
    }
  }

  static class RecommendationCreationEvent extends RecommendationEvent {
    private RecommendationCreationEvent(Recommendation recommendation,
                                        Episode episode,
                                        User user) {
      super(recommendation, episode, user);
    }
  }

  static class RecommendationDeletionEvent extends RecommendationEvent {
    private RecommendationDeletionEvent(Recommendation recommendation,
                                        Episode episode,
                                        User user) {
      super(recommendation, episode, user);
    }
  }

}
