package podcast.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import podcast.models.entities.Following;
import podcast.models.entities.User;
import podcast.models.formats.Failure;
import podcast.models.formats.Result;
import podcast.models.formats.Success;
import podcast.services.FollowersFollowingsService;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import static podcast.utils.Constants.*;

@RestController
@RequestMapping("/api/v1/followings")
public class FollowingsController {

  private final FollowersFollowingsService ffService;

  @Autowired
  public FollowingsController(FollowersFollowingsService ffService) {
    this.ffService = ffService;
  }


  /** Create a following - User A follows user B **/
  @RequestMapping(method = RequestMethod.POST, value = "/{id}")
  public ResponseEntity<Result> createFollowing(HttpServletRequest request,
                                                @PathVariable(value = "id") String id) {

    /* Grab the user corresponding to the request */
    User user = (User) request.getAttribute(USER);

    try {
      Following following = ffService.createFollowing(user, id);
      return ResponseEntity.status(200).body(
          new Success(FOLLOWING, following));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(400).body(new Failure(e.getMessage()));
    }
  }


  /** Get the list of people a given user is following **/
  @RequestMapping(method = RequestMethod.GET, value = "/show")
  public ResponseEntity<Result> getUserFollowings(HttpServletRequest request,
                                                  @RequestParam(value = "id") String id) {
    /* Grab the user corresponding to the request */
    User user = (User) request.getAttribute(USER);

    try {
      List<Following> followings = id.equals("me") ?
        ffService.getUserFollowings(user.getId()) :
        ffService.getUserFollowings(id);
      return ResponseEntity.status(200).body(new Success(FOLLOWINGS, followings));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(400).body(new Failure(e.getMessage()));
    }
  }


  /** Deletes a following **/
  @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
  public ResponseEntity<Result> deleteFollowing(HttpServletRequest request,
                                                @PathVariable(value = "id") String id) {

    /* Grab the user corresponding to the request */
    User user = (User) request.getAttribute(USER);

    try {
      boolean deleted = ffService.deleteFollowing(user, id);
      return ResponseEntity.status(200).body(new Success());
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(400).body(new Failure(e.getMessage()));
    }
  }
}

