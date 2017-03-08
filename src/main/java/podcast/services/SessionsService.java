package podcast.services;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.JsonDocument;
import org.springframework.stereotype.Service;
import podcast.models.entities.Session;
import podcast.models.entities.User;

/**
 * Service to handle storage and querying of
 * session information in Couchbase
 */
@Service
public class SessionsService {

  /** Grab a user from a session **/
  public User userFromSessionToken(Bucket bucket, String sessionToken) {
    String userId = Session.tokenToUserId(sessionToken);
    JsonDocument result = bucket.get(userId);
    User user = new User(result.content());
    // TODO -
    // Check user.getSession().getSessionToken().equals(sessionToken)
    // Check to see if current time is before expiration time
    return user;
  }

}
