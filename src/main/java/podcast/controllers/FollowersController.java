package podcast.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import podcast.models.utils.Constants;
import podcast.services.FollowersFollowingsService;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/api/v1/followers/")
public class FollowersController {

  private final FollowersFollowingsService ffService;

  @Autowired
  public FollowersController(FollowersFollowingsService ffService) {
    this.ffService = ffService;
  }

  @RequestMapping(method = RequestMethod.POST, value = "/")
  public ResponseEntity<Result> getUserFollowings(HttpServletRequest request,
                                                  @RequestParam(value = Constants.ID) String id) {

    Optional<List<Follower>> followers;
    User user = (User) request.getAttribute(Constants.USER);

    if (id.equals("me")) {
      followers = ffService.getUserFollowers(user.getId());
    } else {
      followers = ffService.getUserFollowers(id);
    }
    try {
      return ResponseEntity.status(200).body(
          new Success(Constants.FOLLOWERS, followers.orElse(new ArrayList<Follower>())));
    } catch (Exception e) {
      return ResponseEntity.status(400).body(new Failure(e.getMessage()));
    }
  }
}
