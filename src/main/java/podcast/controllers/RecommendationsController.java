package podcast.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import podcast.models.entities.recommendations.Recommendation;
import podcast.models.entities.users.User;
import podcast.models.formats.Failure;
import podcast.models.formats.Result;
import podcast.models.formats.Success;
import podcast.services.RecommendationsService;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import static podcast.utils.Constants.*;

/**
 * Podcast episode recommendations REST API controller
 */
@RestController
@RequestMapping("/api/v1/recommendations")
public class RecommendationsController {

  private final RecommendationsService recommendationsService;

  @Autowired
  public RecommendationsController(RecommendationsService recommendationsService) {
    this.recommendationsService = recommendationsService;
  }

  /** Create a recommendation **/
  @RequestMapping(method = RequestMethod.POST, value = "/{episode_id}")
  public ResponseEntity<Result> createRecommendation(HttpServletRequest request,
                                                     @PathVariable("episode_id") String episodeId) {
    /* Grab the user from the corresponding request */
    User user = (User) request.getAttribute(USER);
    try {
      Recommendation recommendation = recommendationsService.createRecommendation(user, episodeId);
      return ResponseEntity.status(200).body(new Success(RECOMMENDATION, recommendation));
    } catch (Exception e) {
      return ResponseEntity.status(400).body(new Failure(e.getMessage()));
    }
  }

  /** Get recommendations of an episode (paginated) **/
  @RequestMapping(method = RequestMethod.GET, value = "/{episode_id}")
  public ResponseEntity<Result> getRecommendations(HttpServletRequest request,
                                                   @PathVariable("episode_id") String episodeId,
                                                   @RequestParam("offset") Integer offset,
                                                   @RequestParam("max") Integer max) {
    try {
      // TODO
      return ResponseEntity.status(200).body(new Success(RECOMMENDATIONS, null));
    } catch (Exception e) {
      return ResponseEntity.status(400).body(new Failure(e.getMessage()));
    }
  }

  /** Delete a recommendation **/
  @RequestMapping(method = RequestMethod.DELETE, value = "/{episode_id}")
  public ResponseEntity<Result> deleteRecommendation(HttpServletRequest request,
                                                     @PathVariable("episode_id") String episodeId) {
    User user = (User) request.getAttribute(USER);
    try {
      Recommendation recommendation = recommendationsService.deleteRecommendation(user, episodeId);
      return ResponseEntity.status(200).body(new Success(RECOMMENDATION, recommendation));
    } catch (Exception e) {
      return ResponseEntity.status(400).body(new Failure(e.getMessage()));
    }
  }

  /** Get the recommendations of a user by Id */
  @RequestMapping(method = RequestMethod.GET, value = "/users/{user_id}")
  public ResponseEntity<Result> getUserRecommendations(HttpServletRequest request,
                                                       @PathVariable("user_id") String userId) {
    try {
      List<Recommendation> recommendations = recommendationsService.getUserRecommendations(userId);
      return ResponseEntity.status(200).body(new Success(RECOMMENDATIONS, recommendations));
    } catch (Exception e) {
      return ResponseEntity.status(400).body(new Failure(e.getMessage()));
    }
  }
}
