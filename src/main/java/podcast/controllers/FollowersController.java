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

    User user = (User) request.getAttribute(Constants.USER);

    if(id.equals("me")) {

      ffService.getUserFollowers(user.getId());
      try {
        return ResponseEntity.status(200).body(
            new Success(Constants.USER, user));
      } catch (Exception e) {
        return ResponseEntity.status(400).body(new Failure(e.getMessage()));
      }
    }
    else {
      try {
        ffService.getUserFollowers(id);
        return ResponseEntity.status(200).body(
            new Success(Constants.USER, user));
      } catch (Exception e) {
        return ResponseEntity.status(400).body(new Failure(e.getMessage()));
      }
    }
  }
}
