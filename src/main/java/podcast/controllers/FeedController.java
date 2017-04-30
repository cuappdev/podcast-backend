package podcast.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import podcast.models.entities.feeds.FeedElement;
import podcast.models.entities.users.User;
import podcast.models.formats.Failure;
import podcast.models.formats.Result;
import podcast.models.formats.Success;
import podcast.services.FeedElementService;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import static podcast.utils.Constants.*;

/**
 * Feed REST API Controller
 */
@RestController
@RequestMapping("/api/v1/feed")
public class FeedController {

  private final FeedElementService feedElementService;

  @Autowired
  public FeedController(FeedElementService feedElementService) {
    this.feedElementService = feedElementService;
  }

  @RequestMapping(method = RequestMethod.GET, value = "")
  public ResponseEntity<Result> getFeed(HttpServletRequest request,
                                        @RequestParam(value = "offset") Integer offset,
                                        @RequestParam(value = "max") Integer max) {
    User user = (User) request.getAttribute(USER);
    try {
      List<FeedElement> feeds = feedElementService.getFeedElements(user.getId(), offset, max);
      return ResponseEntity.status(200).body(new Success(FEEDS, feeds));
    } catch (Exception e) {
      return ResponseEntity.status(400).body(new Failure(e.getMessage()));
    }
  }
}
