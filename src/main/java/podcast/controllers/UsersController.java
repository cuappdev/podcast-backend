package podcast.controllers;

import com.couchbase.client.java.Bucket;
import org.codehaus.jackson.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import podcast.models.entities.Session;
import podcast.models.entities.User;
import podcast.models.utils.Response;
import podcast.services.GoogleService;
import podcast.services.UsersService;
import java.util.Optional;

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


  /**
   * Google Sign In Endpoint
   * @param idToken - Google ID
   * @return - Response
   */
  @RequestMapping(method = RequestMethod.POST, value = "/google_sign_in")
  public Response googleSignIn(@RequestParam(value="id_token", required = true) String idToken) {
    // Grab google
    JsonNode response = googleService.googleAuthentication(idToken);
    String googleID = googleService.googleIDFromResponse(response);

    // Check if user exists
    Optional<User> possUser = usersService.getUserByGoogleID(bucket, googleID);

    // If exists, return, else make new user
    Response r;
    if (possUser.isPresent()) {
      r = new Response(true, "user", possUser.get());
      r.addField("newUser", false);
      return r;
    } else {
      User user = usersService.createUser(bucket, response);
      r = new Response(true, "user", user);
      r.addField("newUser", true);
      return r;
    }
  }


  // TODO - More user endpoints

}
