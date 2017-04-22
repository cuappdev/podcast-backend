package podcast.repos;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.JsonDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import podcast.models.entities.users.User;
import rx.Observable;
import java.util.List;
import java.util.Optional;

@Component
public class UsersRepo {

  /* Connection to DB */
  private Bucket bucket;

  @Autowired
  public UsersRepo(@Qualifier("dbBucket") Bucket dbBucket) {
    this.bucket = dbBucket;
  }

  /** Store a user in the bucket **/
  public User storeUser(User user) {
    // Add docs for batch insert
    List<JsonDocument> docs = user.docs();
    // Insert them in one batch, waiting until the last one is done
    Observable
      .from(docs)
      .flatMap(jsonDocument -> bucket.async().upsert(jsonDocument))
      .last()
      .toBlocking()
      .single();

    return user;
  }

  /** Remove User by ID (+ return the user just deleted) **/
  public void removeUserById(String id) throws Exception {
    JsonDocument doc = bucket.get(User.composeKey(id));
    if (doc == null) {
      throw new Exception("This user does not exist");
    }

    User user = new User(doc.content());
    List<String> keys = user.keys();
    // Remove them batch-wise
    Observable
      .from(keys)
      .flatMap(s -> bucket.async().remove(s))
      .last()
      .toBlocking()
      .single();
  }

  /** Checks to see if this username is available **/
  public boolean usernameAvailable(String username) {
    JsonDocument doc = bucket.get(User.UsernameToUser.composeKey(username));
    return doc == null;
  }

  /** Update user username **/
  public User updateUsername(User user, String username) throws Exception {
    // Remove previous lookup for username
    bucket.remove(User.UsernameToUser.composeKey(user.getUsername()));
    // Sets username, ensures it's valid
    user.setUsername(username);
    // Stores user as a whole + returns the user
    return storeUser(user);
  }

  /** Get User by ID **/
  public User getUserById(String id) throws Exception {
    JsonDocument doc = bucket.get(User.composeKey(id));
    if (doc == null) {
      throw new Exception();
    } else {
      return new User(doc.content());
    }
  }

  /** Get User by Google ID (optional) **/
  public Optional<User> getUserByGoogleId(String googleId) {
    JsonDocument doc = bucket.get(User.GoogleIdToUser.composeKey(googleId));
    if (doc == null) {
      return Optional.empty();
    } else {
      User.GoogleIdToUser idToUser = new User.GoogleIdToUser(doc.content());
      User user = new User(bucket.get(User.composeKey(idToUser.getUserId())).content());
      return Optional.of(user);
    }
  }

}
