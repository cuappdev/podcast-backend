package podcast.controllers;

import com.couchbase.client.java.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import podcast.models.entities.User;
import podcast.models.formats.Failure;
import podcast.models.formats.Result;
import podcast.models.formats.Success;
import podcast.models.utils.Constants;
import podcast.services.SessionsService;

/**
 * Sessions REST API controller
 */
@RestController
@RequestMapping("/api/v1/sessions")
public class SessionsController {

  private final Bucket bucket;
  private final SessionsService sessionsService;


  @Autowired
  public SessionsController(@Qualifier("usersBucket") Bucket bucket,
                            SessionsService sessionsService) {
    this.bucket = bucket;
    this.sessionsService = sessionsService;
  }


  /** Update the session of a user + return the whole user **/
  @RequestMapping(method = RequestMethod.POST, value = "update")
  public ResponseEntity<Result> updateSession(
    @RequestHeader(value = "UpdateToken", required = true) String updatetoken) {
    try {
      User user = sessionsService.updateSession(bucket, updatetoken);
      return ResponseEntity.status(200).body(new Success(Constants.USER, user));
    } catch (Exception e) {
      return ResponseEntity.status(403).body(new Failure(e.getMessage()));
    }
  }


}
