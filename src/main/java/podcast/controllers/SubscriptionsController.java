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
import podcast.services.SubscriptionsService;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

import static podcast.utils.Constants.*;

/**
 * Subscription REST API controller
 */
@RestController
@RequestMapping("/api/v1/subscriptions")
public class SubscriptionsController {

  private final PodcastsService podcastsService;
  private final SubscriptionsService subscriptionsService;

  @Autowired
  public SubscriptionsController(PodcastsService podcastsService, SubscriptionsService subscriptionsService) {
    this.podcastsService = podcastsService;
    this.subscriptionsService = subscriptionsService;
  }

  /** Create a subscription **/
  @RequestMapping(method = RequestMethod.POST, value = "")
  public ResponseEntity<Result> createSubscription(HttpServletRequest request,
                                                     @RequestParam("series_id") Long seriesId) {
    User user = (User) request.getAttribute(USER);

    try {
      Series series = podcastsService.getSeries(seriesId);
      Subscription subscription = subscriptionsService.createSubscription(user, series);
      return ResponseEntity.status(200).body(new Success(SUBSCRIPTION, subscription));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(400).body(new Failure(e.getMessage()));
    }
  }

  /** Delete a subscription **/
  @RequestMapping(method = RequestMethod.DELETE, value = "")
  public ResponseEntity<Result> deleteSubscription(HttpServletRequest request,
                                                   @RequestParam("series_id") Long seriesId) {
    User user = (User) request.getAttribute(USER);

    try {
      Series series = podcastsService.getSeries(seriesId);
      Subscription subscription = new Subscription(user, series);
      subscriptionsService.deleteSubscription(subscription);
      return ResponseEntity.status(200).body(new Success(SUBSCRIPTION, subscription));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(400).body(new Failure(e.getMessage()));
    }
  }

  /** Get a series of subscriptions of a user **/
  @RequestMapping(method = RequestMethod.GET, value = "")
  public ResponseEntity<Result> getUserSubscriptions(HttpServletRequest request,
                                                   @RequestParam("id") String userId) {
    try {
      List<Subscription> subs = subscriptionsService.getUserSubscriptions(userId);
      return ResponseEntity.status(200).body(new Success(SUBSCRIPTIONS, subs));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(400).body(new Failure(e.getMessage()));
    }
  }
}
