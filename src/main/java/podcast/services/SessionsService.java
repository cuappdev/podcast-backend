package podcast.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import podcast.models.entities.sessions.Session;
import podcast.models.entities.users.User;
import podcast.repos.UsersRepo;

/**
 * Service to handle storage and querying of
 * session information in Couchbase
 */
@Service
public class SessionsService {

  /* DB operations */
  private UsersRepo usersRepo;

  @Autowired
  public SessionsService (UsersRepo usersRepo) {
    this.usersRepo = usersRepo;
  }

  /** Grab the user by the update token,
   * update the session and save the new user **/
  public User updateSession(String updateToken) throws Exception {
    String userId = Session.tokenToUserId(updateToken);
    User user = usersRepo.getUserById(userId);
    if (!user.getSession().getUpdateToken().equals(updateToken)) {
      throw new InvalidUpdateTokenException();
    }
    user.setSession(new Session(user));
    usersRepo.storeUser(user);
    return user;
  }

  /** When update token is invalid **/
  public class InvalidUpdateTokenException extends Exception {
    public InvalidUpdateTokenException() {
      super("This update token is invalid");
    }
  }

}
