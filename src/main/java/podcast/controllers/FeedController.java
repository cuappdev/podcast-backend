package podcast.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import podcast.services.FeedElementService;

/**
 * Feed REST API Controller
 */
@RestController
@RequestMapping("/api/v1/feed/")
public class FeedController {

  private final FeedElementService feedElementService;

  @Autowired
  public FeedController(FeedElementService feedElementService) {
    this.feedElementService = feedElementService;
  }

  // TODO - endpoints


}
