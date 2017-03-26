package podcast.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
  @RequestMapping(method = RequestMethod.POST, value = "/{episode_id}")
  public ResponseEntity<Result> createRecommendation(HttpServletRequest request,
                                                     @PathVariable("episode_id") String episodeId) {
    /* Grab the user from the corresponding request */
    User user = (User) request.getAttribute(USER);
    try {
      Episode.CompositeEpisodeKey ck = Episode.getSeriesIdAndPubDate(episodeId);
      Episode episode = podcastsService.getEpisode(ck.getSeriesId(), ck.getPubDate());
      Recommendation recommendation = new Recommendation(user, episode);
      // TODO - Save recommendation
      return ResponseEntity.status(200).body(new Success(RECOMMENDATION, recommendation));
    } catch (Exception e) {
      return ResponseEntity.status(400).body(new Failure(e.getMessage()));
    }
  }




}
