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
import podcast.models.formats.Result;
import podcast.models.formats.Success;
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

  /** Check session validity **/
  @RequestMapping(method = RequestMethod.GET, value = "/check")
  public ResponseEntity<Result> check(@RequestHeader(value = "SESSION_TOKEN", required = true) String sessionToken) {
    User user = sessionsService.userFromSessionToken(bucket, sessionToken);
    return ResponseEntity.status(200).body(new Success("user", user));
  }

}
