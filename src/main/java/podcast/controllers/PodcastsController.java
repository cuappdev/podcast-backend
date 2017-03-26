package podcast.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import podcast.models.entities.User;
import podcast.models.formats.Failure;
import podcast.models.formats.Result;
import podcast.models.formats.Success;
import javax.servlet.http.HttpServletRequest;
import static podcast.utils.Constants.*;

/**
 * Podcasts (series, episodes) REST API controller
 */
@RestController
@RequestMapping("/api/v1/podcasts")
public class PodcastsController {

  /** Get episodes by seriesId **/
  @RequestMapping(method = RequestMethod.GET, value = "/{series_id}")
  public ResponseEntity<Result> getEpisodesBySeriesId(HttpServletRequest request,
                                                      @PathVariable("series_id") String seriesId,
                                                      @RequestParam("offset") Integer offset,
                                                      @RequestParam("max") Integer max) {
    /* Grab the user from the corresponding request */
    User user = (User) request.getAttribute(USER);
    try {
      // TODO - get episodes, sorted by release date
      return ResponseEntity.status(200).body(new Success());
    } catch (Exception e) {
      return ResponseEntity.status(400).body(new Failure(e.getMessage()));
    }
  }

}
