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


  /** Get user by Google ID **/
  public Optional<User> getUserByGoogleId(String googleId) {
    return usersRepo.getUserByGoogleId(googleId);
  }


  /** Update the username of a user **/
  public User updateUsername(User user,
                             String username) throws User.InvalidUsernameException {
    // TODO - check duplicate usernames amongst users
    user.setUsername(username.toLowerCase());
    usersRepo.storeUser(user);
    return user;
  }



}