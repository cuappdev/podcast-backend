package podcast.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import podcast.models.entities.Follower;
import podcast.models.entities.User;
import podcast.models.formats.Failure;
import podcast.models.formats.Result;
import podcast.models.formats.Success;
import podcast.services.FollowersFollowingsService;
import javax.servlet.http.HttpServletRequest;
import static podcast.utils.Constants.*;

@RestController
@RequestMapping("/api/v1/followers/")
public class FollowersController {

  private final FollowersFollowingsService ffService;

  @Autowired
  public FollowersController(FollowersFollowingsService ffService) {
    this.ffService = ffService;
  }

  @RequestMapping(method = RequestMethod.GET, value = "/show")
  public ResponseEntity<Result> getUserFollowings(HttpServletRequest request,
                                                  @RequestParam(value = "id") String id) {
    /* Grab the user corresponding to the request */
    User user = (User) request.getAttribute(USER);

    try {
      List<Follower> followers = id.equals("me") ?
        ffService.getUserFollowers(user.getId()) :
        ffService.getUserFollowers(id);
      return ResponseEntity.status(200).body(new Success(FOLLOWERS, followers));
    } catch (Exception e) {
      return ResponseEntity.status(400).body(new Failure(e.getMessage()));
    }
  }
}
