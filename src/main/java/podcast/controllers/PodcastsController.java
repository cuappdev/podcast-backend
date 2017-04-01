package podcast.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import podcast.models.entities.Episode;
import podcast.models.entities.Series;
import podcast.models.entities.User;
import podcast.models.formats.Failure;
import podcast.models.formats.Result;
import podcast.models.formats.Success;
import podcast.services.PodcastsService;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import static podcast.utils.Constants.*;

/**
 * Podcasts (series, episodes) REST API controller
 */
@RestController
@RequestMapping("/api/v1/podcasts")
public class PodcastsController {

  private final PodcastsService podcastsService;

  @Autowired
  public PodcastsController(PodcastsService podcastsService) {
    this.podcastsService = podcastsService;
  }

  /** Get a series by seriesId **/
  @RequestMapping(method = RequestMethod.GET, value = "/series/{series_id}")
  public ResponseEntity<Result> getSeriesById(HttpServletRequest request,
                                              @PathVariable("series_id") Long seriesId) {
    User user = (User) request.getAttribute(USER);
    try {
      Series series = podcastsService.getSeries(user, seriesId);
      return ResponseEntity.status(200).body(new Success(SERIES, series));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(400).body(new Failure(e.getMessage()));
    }
  }


  /** Get episodes by seriesId **/
  @RequestMapping(method = RequestMethod.GET, value = "/episodes/{series_id}")
  public ResponseEntity<Result> getEpisodesBySeriesId(HttpServletRequest request,
                                                      @PathVariable("series_id") Long seriesId,
                                                      @RequestParam("offset") Integer offset,
                                                      @RequestParam("max") Integer max) {
    try {
      List<Episode> episodes = podcastsService.getEpisodesBySeriesId(seriesId, offset, max);
      return ResponseEntity.status(200).body(new Success(EPISODES, episodes));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(400).body(new Failure(e.getMessage()));
    }
  }

}
