package podcast.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import podcast.models.entities.User;
import podcast.models.formats.Failure;
import podcast.models.formats.Result;
import podcast.models.formats.Success;
import podcast.models.utils.Constants;
import podcast.services.FollowersFollowingsService;
import podcast.services.GoogleService;
import podcast.services.UsersService;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/followings/")
public class FollowingsController {

  private final FollowersFollowingsService ffService;

  @Autowired
  public FollowingsController(FollowersFollowingsService ffService) {
    this.ffService = ffService;
  }

  /** Create a following.
   * This is the endpoint we want to call when a user follows another.
   * @param request
   * @param id the user being followed
   * @return
   */
  @RequestMapping(method = RequestMethod.POST, value = "/new")
  public ResponseEntity<Result> newFollowing(HttpServletRequest request,
                                               @RequestParam(value = Constants.ID) String id) {

    /* Grab the user corresponding to the request */
    User user = (User) request.getAttribute(Constants.USER);

    try {
      ffService.createFollowing(user, id);
      return ResponseEntity.status(200).body(
          new Success(Constants.USER, user));
    } catch (Exception e) {
      return ResponseEntity.status(400).body(new Failure(e.getMessage()));
    }
  }

}
