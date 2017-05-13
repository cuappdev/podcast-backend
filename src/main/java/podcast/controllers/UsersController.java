package podcast.controllers;

import org.codehaus.jackson.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import podcast.models.entities.users.Person;
import podcast.models.entities.users.User;
import podcast.models.formats.Failure;
import podcast.models.formats.Result;
import podcast.models.formats.Success;
import podcast.services.GoogleService;
import podcast.services.UsersService;
import javax.servlet.http.HttpServletRequest;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;

import static podcast.utils.Constants.*;

/**
 * Users REST API controller
 */
@RestController
@RequestMapping("/api/v1/users")
public class UsersController {

  private final GoogleService googleService;
  private final UsersService usersService;

  @Autowired
  public UsersController(GoogleService googleService,
                         UsersService usersService) {
    this.googleService = googleService;
    this.usersService = usersService;
  }

  /** Google Sign In endpoint **/
  @RequestMapping(method = RequestMethod.POST, value = "/google_sign_in")
  public ResponseEntity<Result> googleSignIn(@RequestParam(value="id_token") String idToken) {
    try {
      /* Grab Google API response */
      JsonNode response = googleService.googleAuthentication(idToken);
      String googleId = googleService.googleIdFromResponse(response);
      AbstractMap.SimpleEntry<Boolean, User> r = usersService.getOrCreateUser(response, googleId);
      return ResponseEntity.status(200)
        .body(new Success(USER, r.getValue()).addField(NEW_USER, r.getKey()));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(400).body(new Failure(e.getMessage()));
    }
  }

  /** Change username **/
  @RequestMapping(method = RequestMethod.POST, value = "/change_username")
  public ResponseEntity<Result> changeUsername(HttpServletRequest request,
                                               @RequestParam(value=USERNAME) String username) {
    User user = (User) request.getAttribute(USER);
    try {
      usersService.updateUsername(user, username);
      return ResponseEntity.status(200).body(
        new Success(USER, user));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(400).body(new Failure(e.getMessage()));
    }
  }

  /** Get a user by Id **/
  @RequestMapping(method = RequestMethod.GET, value = "/{id}")
  public ResponseEntity<Result> userById(HttpServletRequest request,
                                         @PathVariable("id") String id) {
    User user = (User) request.getAttribute(USER);
    try {
      if (user.getId().equals(id)) {
        return ResponseEntity.status(200).body(new Success(USER, user));
      } else {
        Person peer = new Person(usersService.getUserById(id));
        return ResponseEntity.status(200).body(new Success(USER, peer));
      }
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(400).body(new Failure(e.getMessage()));
    }
  }

  /** Users info */
  @RequestMapping(method = RequestMethod.GET, value="/info")
  public ResponseEntity<Result> usersInfo(HttpServletRequest request,
                                          @RequestParam("ids") String ids) {
    User user = (User) request.getAttribute(USER);
    try {
      List<String> idList = Arrays.asList(ids.split(","));
      UsersService.UsersInfo usersInfo = usersService.getUsersInfo(user.getId(), idList);
      return ResponseEntity.status(200).body(new Success(USERS_INFO, usersInfo));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(400).body(new Failure(e.getMessage()));
    }
  }
}
