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

  public Recommendation createRecommendation(User owner, String episodeId) {
    Episode.CompositeEpisodeKey comp = Episode.getSeriesIdAndPubDate(episodeId);
    Episode episode = podcastsRepo.getEpisodeBySeriesIdAndTimestamp(comp.getSeriesId(), comp.getPubDate());
    Recommendation recommendation = new Recommendation(owner, episode);
    publisher.publishEvent(new RecommendationCreationEvent(recommendation, episode, owner));
    return recommendationsRepo.storeRecommendation(recommendation, episode);
  }

  public boolean deleteRecommendation(User owner, String episodeId) {
    Episode.CompositeEpisodeKey comp = Episode.getSeriesIdAndPubDate(episodeId);
    Episode episode = podcastsRepo.getEpisodeBySeriesIdAndTimestamp(comp.getSeriesId(), comp.getPubDate());
    Recommendation recommendation = recommendationsRepo.getRecommendation(owner, episodeId);
    publisher.publishEvent(new RecommendationDeletionEvent(recommendation, episode, owner));
    return recommendationsRepo.deleteRecommendation(recommendation, episode);
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
