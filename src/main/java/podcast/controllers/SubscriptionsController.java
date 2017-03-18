package podcast.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import podcast.models.entities.*;
import podcast.models.formats.Failure;
import podcast.models.formats.Result;
import podcast.models.formats.Success;
import podcast.services.PodcastsService;

import javax.servlet.http.HttpServletRequest;

import static podcast.utils.Constants.*;

/**
 * Subscription REST API controller
 */
@RestController
@RequestMapping("/api/v1/subscriptions")
public class SubscriptionsController {

  private final PodcastsService podcastsService;

  @Autowired
  public SubscriptionsController(PodcastsService podcastsService) {
    this.podcastsService = podcastsService;
  }

  /** Create a recommendation **/
  @RequestMapping(method = RequestMethod.POST, value = "/create")
  public ResponseEntity<Result> createSubscription(HttpServletRequest request,
                                                     @RequestParam("series_id") Long seriesId) {
    /* Grab the user from the corresponding request */
    User user = (User) request.getAttribute(USER);

    try {
      Series series = podcastsService.getSeries(seriesId);
      Subscription subscription = new Subscription(user, series);
      // TODO - Save subscription
      return ResponseEntity.status(200).body(new Success(SUBSCRIPTION, subscription));
    } catch (Exception e) {
      return ResponseEntity.status(400).body(new Failure(e.getMessage()));
    }
  }
}
