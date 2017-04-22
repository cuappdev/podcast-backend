package podcast.services;

import org.codehaus.jackson.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import podcast.models.entities.sessions.Session;
import podcast.models.entities.users.User;
import podcast.repos.UsersRepo;
import rx.Observable;

import java.util.AbstractMap;
import java.util.Optional;
import static podcast.utils.Lambdas.*;

/**
 * Service to handle storage and querying of
 * user information in Couchbase
 */
@Service
public class UsersService {

  private UsersRepo usersRepo;

  @Autowired
  public UsersService(UsersRepo usersRepo) {
    this.usersRepo = usersRepo;
  }

  /** Get or create User, given a response from Google API -
   * boolean indicates whether or not the user is new **/
  public AbstractMap.SimpleEntry<Boolean, User> getOrCreateUser(JsonNode response, String googleId) {
    // Must be an atomic attempt
    synchronized (this) {
      Optional<User> possUser = usersRepo.getUserByGoogleId(googleId);
      User user = possUser.isPresent() ? possUser.get() : new User(response);
      Boolean newUser = !possUser.isPresent();
      user.setSession(new Session(user));
      usersRepo.storeUser(user);
      return new AbstractMap.SimpleEntry<Boolean, User>(newUser, user);
    }
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
                             String username) throws Exception {
    // Must be atomic, we can't have one thread check and update while
    // the other is doing the same
    synchronized (this) {
      if (usersRepo.usernameAvailable(username)) {
        usersRepo.updateUsername(user, username);
        return user;
      } else {
        throw new User.UsernameTakenException();
      }
    }
  }

  /** Remove user by ID **/
  public void removeUserById(String id) throws Exception {
    usersRepo.removeUserById(id);
  }

  // MARK - listeners

  @EventListener
  private void handleFollowingCreation(FollowersFollowingsService.FollowingCreationEvent creationEvent) {

    // Update the owner
    Observable<User> updateOwner = Observable.defer(() -> {
      try {
        return Observable.just(usersRepo.getUserById(creationEvent.owner.getId()));
      } catch (Exception e) {
        return null;
      }
    }).map(owner -> {
      owner.incrementFollowings();
      return owner;
    }).flatMap(owner -> Observable.just(usersRepo.replaceUser(owner)))
      .retryWhen(attempts -> retry.operation(attempts));

    // Update the followed
    Observable<User> updateFollowed = Observable.defer(() -> {
      try {
        return Observable.just(usersRepo.getUserById(creationEvent.followed.getId()));
      } catch (Exception e) {
        return null;
      }
    }).map(followed -> {
      followed.incrementFollowers();
      return followed;
    }).flatMap(followed -> Observable.just(usersRepo.replaceUser(followed)))
      .retryWhen(attempts -> retry.operation(attempts));

    // Merge these + subscribe (start)
    Observable.merge(updateOwner, updateFollowed).subscribe();
  }

  @EventListener
  private void handleFollowingDeletion(FollowersFollowingsService.FollowingDeletionEvent deletionEvent) {

    // Update the owner
    Observable<User> updateOwner = Observable.defer(() -> {
      try {
        return Observable.just(usersRepo.getUserById(deletionEvent.owner.getId()));
      } catch (Exception e) {
        return null;
      }
    }).map(owner -> {
      owner.decrementFollowings();
      return owner;
    }).flatMap(owner -> Observable.just(usersRepo.replaceUser(owner)))
      .retryWhen(attempts -> retry.operation(attempts));

    // Update the followed
    Observable<User> updateFollowed = Observable.defer(() -> {
      try {
        return Observable.just(usersRepo.getUserById(deletionEvent.followed.getId()));
      } catch (Exception e) {
        return null;
      }
    }).map(followed -> {
      followed.decrementFollowers();
      return followed;
    }).flatMap(followed -> Observable.just(usersRepo.replaceUser(followed)))
      .retryWhen(attempts -> retry.operation(attempts));

    // Merge these + subscribe (start)
    Observable.merge(updateOwner, updateFollowed).subscribe();
  }

}