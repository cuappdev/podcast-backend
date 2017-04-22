package podcast.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import podcast.models.entities.podcasts.Episode;
import podcast.models.entities.recommendations.Recommendation;
import podcast.models.entities.users.User;
import podcast.repos.PodcastsRepo;
import podcast.repos.RecommendationsRepo;
import podcast.repos.UsersRepo;

@Service
public class RecommendationsService {

  private PodcastsRepo podcastsRepo;
  private UsersRepo usersRepo;
  private RecommendationsRepo recommendationsRepo;

  @Autowired
  public RecommendationsService(PodcastsRepo podcastsRepo,
                                UsersRepo usersRepo,
                                RecommendationsRepo recommendationsRepo) {
    this.podcastsRepo = podcastsRepo;
    this.usersRepo = usersRepo;
    this.recommendationsRepo = recommendationsRepo;
  }

  public Recommendation createRecommendation(User owner, Episode episode) {
    synchronized (this) {
      Recommendation recommendation = new Recommendation(owner, episode);
      recommendationsRepo.storeRecommendation(recommendation, episode);
      return recommendation;
    }
  }

  public boolean deleteRecommendation(Recommendation recommendation) {
    synchronized (this) {
      Episode.CompositeEpisodeKey comp = Episode.getSeriesIdAndPubDate(recommendation.getEpisodeId());
      return recommendationsRepo.deleteRecommendation(recommendation,
        podcastsRepo.getEpisodeBySeriesIdAndTimestamp(comp.getSeriesId(), comp.getPubDate()));
    }
  }


}
