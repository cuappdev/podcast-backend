package podcast.controllers;

import com.couchbase.client.java.Bucket;
import org.codehaus.jackson.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import podcast.services.GoogleService;
import podcast.services.UsersService;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;


/**
 * Users REST API controller
 */
@RestController
@RequestMapping("/api/v1/users")
public class UsersController {

  private final Bucket bucket;
  private final GoogleService googleService;
  private final UsersService usersService;

  @Autowired
  public UsersController(@Qualifier("usersBucket") Bucket bucket,
                         GoogleService googleService,
                         UsersService usersService) {
    this.bucket = bucket;
    this.googleService = googleService;
    this.usersService = usersService;
  }


  /** Google Sign In endpoint **/
  @RequestMapping(method = RequestMethod.POST, value = "/google_sign_in")
  public ResponseEntity<Result> googleSignIn(@RequestParam(value="id_token", required = true) String idToken) {
    /* Grab Google API response */
    JsonNode response = googleService.googleAuthentication(idToken);
    String googleID = googleService.googleIDFromResponse(response);

    /* Check if user exists */
    Optional<User> possUser = usersService.getUserByGoogleId(bucket, googleID);

    /* If exists, return, else make new user */
    Success r;
    if (possUser.isPresent()) {
      r = new Success(Constants.USER, possUser.get());
      r.addField(Constants.NEW_USER, false);
      return ResponseEntity.status(200).body(r);
    } else {
      User user = usersService.createUser(bucket, response);
      r = new Success(Constants.USER, user);
      r.addField(Constants.NEW_USER, true);
      return ResponseEntity.status(200).body(r);
    }
  }


  /** Change username **/
  @RequestMapping(method = RequestMethod.POST, value = "/change_username")
  public ResponseEntity<Result> changeUsername(HttpServletRequest request,
                                               @RequestParam(value=Constants.USERNAME, required = true) String username) {
    /* Grab the user corresponding to the request */
    User user = (User) request.getAttribute(Constants.USER);

    /* If the username is valid */
    try {
      usersService.updateUsername(bucket, user, username);
      return ResponseEntity.status(200).body(
        new Success(Constants.USER, user));
    }
    /* If the username is invalid */
    catch (User.InvalidUsernameException e) {
      return ResponseEntity.status(500).body(new Failure(
        new ArrayList<String>(Arrays.asList(e.getMessage())))
      );
    }
  }


}
