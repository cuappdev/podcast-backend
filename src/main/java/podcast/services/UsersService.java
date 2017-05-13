package podcast.services;

import lombok.Getter;
import org.codehaus.jackson.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import podcast.models.entities.sessions.Session;
import podcast.models.entities.users.User;
import podcast.repos.FollowersFollowingsRepo;
import podcast.repos.UsersRepo;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Service to handle storage and querying of
 * user information in Couchbase
 */
@Service
public class UsersService {

  private final UsersRepo usersRepo;
  private final FollowersFollowingsRepo followersFollowingsRepo;

  @Autowired
  public UsersService(UsersRepo usersRepo,
                      FollowersFollowingsRepo followersFollowingsRepo) {
    this.usersRepo = usersRepo;
    this.followersFollowingsRepo = followersFollowingsRepo;
  }

  /** Get or create User, given a response from Google API -
   * boolean indicates whether or not the user is new **/
  public AbstractMap.SimpleEntry<Boolean, User> getOrCreateUser(JsonNode response, String googleId) {
    Optional<User> possUser = usersRepo.getUserByGoogleId(googleId);
    User user = possUser.isPresent() ? possUser.get() : new User(response);
    Boolean newUser = !possUser.isPresent();
    user.setSession(new Session(user));
    usersRepo.storeUser(user);
    return new AbstractMap.SimpleEntry<Boolean, User>(newUser, user);
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
  public User updateUsername(User user, String username) throws Exception {
    if (usersRepo.usernameAvailable(username)) {
      usersRepo.updateUsername(user, username);
      return user;
    } else {
      throw new User.UsernameTakenException();
    }
  }

  /** Remove user by ID **/
  public void removeUserById(String id) throws Exception {
    usersRepo.removeUserById(id);
  }

  /** Get users info */
  public UsersInfo getUsersInfo(String userId, List<String> userIds) {
    return new UsersInfo(userId, userIds);
  }

  // MARK - listeners

  @EventListener
  private void handleFollowingCreation(FollowersFollowingsService.FollowingCreationEvent creationEvent) {
    usersRepo.handleFollowingCreation(creationEvent.owner.getId(), creationEvent.followed.getId());
  }

  @EventListener
  private void handleFollowingDeletion(FollowersFollowingsService.FollowingDeletionEvent deletionEvent) {
    usersRepo.handleFollowingDeletion(deletionEvent.owner.getId(), deletionEvent.followed.getId());
  }

  // MARK - Info Wrappers

  public class UsersInfo {
    @Getter private HashMap<String, Boolean> followingsInfo;
    @Getter private HashMap<String, Boolean> followersInfo;

    private UsersInfo(String userId, List<String> userIds) {
      this.followingsInfo = followersFollowingsRepo.getUsersFollowingsMappings(userId, userIds);
      this.followersInfo = followersFollowingsRepo.getUsersFollowersMappings(userId, userIds);
    }
  }

}