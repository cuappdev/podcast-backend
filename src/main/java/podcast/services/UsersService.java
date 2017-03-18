package podcast.services;

import org.codehaus.jackson.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import podcast.models.entities.Session;
import podcast.models.entities.User;
import podcast.repos.UsersRepo;
import java.util.Optional;

/**
 * Service to handle storage and querying of
 * user information in Couchbase
 */
@Service
public class UsersService {

  /* Database communication */
  private UsersRepo usersRepo;

  @Autowired
  public UsersService(UsersRepo usersRepo) {
    this.usersRepo = usersRepo;
  }

  /** Create User, given a response from Google API **/
  public User createUser(JsonNode response) {
    User user = new User(response);
    user.setSession(new Session(user));
    usersRepo.storeUser(user);
    return user;
  }


  /** Get user by Id **/
  public User getUserById(String id) throws Exception {
    return usersRepo.getUserById(id);
  }


  /** Get user by googleId **/
  public Optional<User> getUserByGoogleId(String googleId) {
    return usersRepo.getUserByGoogleId(googleId);
  }


  /** Update the username of a user **/
  public User updateUsername(User user,
                             String username) throws User.InvalidUsernameException {
    // Must be atomic, we can't have one thread check and update while
    // the other is
    synchronized (this) {
      Optional<User> op = usersRepo.getUserByGoogleId(username);

      if (op.isPresent()) {
        throw new User.InvalidUsernameException();
      }

      user.setUsername(username);
      usersRepo.storeUser(user);
      return user;
    }
  }


  /** Remove user by ID **/
  public void removeUserById(String id) throws Exception {
    usersRepo.removeUserById(id);
  }


}