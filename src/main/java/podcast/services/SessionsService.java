package podcast.services;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.JsonDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import podcast.models.entities.Session;
import podcast.models.entities.User;

/**
 * Service to handle storage and querying of
 * session information in Couchbase
 */
@Service
public class SessionsService {

  /* Because we deal with some user operations */
  private UsersService usersService;

  @Autowired
  public SessionsService (UsersService usersService) {
    this.usersService = usersService;
  }


  /** Grab the user by the update token,
   * update the session and save the new user **/
  public User updateSession(Bucket bucket, String updateToken) throws Exception {
    String userId = Session.tokenToUserId(updateToken);
    User user = new User(bucket.get(userId).content());
    if (!user.getSession().getUpdateToken().equals(updateToken)) {
      throw new InvalidUpdateTokenException();
    }
    user.setSession(new Session(user));
    usersService.storeUser(bucket, user);
    return user;
  }

  /** When update token is invalid **/
  public class InvalidUpdateTokenException extends Exception {
    public InvalidUpdateTokenException() {
      super("This update token is invalid");
    }
  }

}
