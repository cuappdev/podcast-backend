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
import podcast.models.formats.Result;
import podcast.models.formats.Success;
import podcast.services.GoogleService;
import podcast.services.UsersService;
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
      r = new Success("user", possUser.get());
      r.addField("newUser", false);
      return ResponseEntity.status(200).body(r);
    } else {
      User user = usersService.createUser(bucket, response);
      r = new Success("user", user);
      r.addField("newUser", true);
      return ResponseEntity.status(200).body(r);
    }
  }


  // TODO - More user endpoints

}
