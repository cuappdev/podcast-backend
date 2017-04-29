package podcast.repos;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FeedElementRepo {

  /** Handle recommendation creation event -
   *  Create a new RecommendationFeedElement or add recommender to the current element **/
  public void handleRecommendationCreation(String episodeId,
                                           String recommenderId,
                                           List<String> followerIds) {

  }

  /** Handle recommendation deletion event -
   * Delete the RecommendationFeedElement or remove the recommender from the current element **/
  public void handleRecommendationDeletion(String episodeId,
                                           String recommenderId,
                                           List<String> followerIds) {
    // TODO
  }
}
