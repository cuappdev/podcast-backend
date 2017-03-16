package podcast.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import podcast.models.entities.Episode;
import podcast.models.entities.Recommendation;
import podcast.models.entities.User;
import podcast.models.formats.Failure;
import podcast.models.formats.Result;
import podcast.models.formats.Success;
import podcast.services.PodcastsService;
import javax.servlet.http.HttpServletRequest;
import static podcast.utils.Constants.*;

/**
 * Podcast episode recommendations REST API controller
 */
@RestController
@RequestMapping("/api/v1/recommendations")
public class RecommendationsController {

  private final PodcastsService podcastsService;

  @Autowired
  public RecommendationsController(PodcastsService podcastsService) {
    this.podcastsService = podcastsService;
  }

  /** Create a recommendation **/
  @RequestMapping(method = RequestMethod.POST, value = "/create")
  public ResponseEntity<Result> createRecommendation(HttpServletRequest request,
                                                     @RequestParam("series_id") Long seriesId,
                                                     @RequestParam("timestamp") Long timestamp) {
    /* Grab the user from the corresponding request */
    User user = (User) request.getAttribute(USER);

    try {
      Episode episode = podcastsService.getEpisode(seriesId, timestamp);
      Recommendation recommendation = new Recommendation(user, episode);
      // TODO - Save recommendation
      return ResponseEntity.status(200).body(new Success(RECOMMENDATION, recommendation));
    } catch (Exception e) {
      return ResponseEntity.status(400).body(new Failure(e.getMessage()));
    }
  }




}
