package podcast.repos;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import podcast.models.entities.User;
import rx.Observable;
import java.util.List;
import java.util.Optional;
import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.dsl.Expression.*;
import static podcast.utils.Constants.*;

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
    JsonDocument doc = bucket.get(id);
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
    JsonDocument doc = bucket.get(username);
    return doc == null;
  }


  /** Update user username **/
  public User updateUsername(User user, String username) throws Exception {
    // Remove previous lookup for username
    bucket.remove(user.getUsername());
    // Sets username, ensures it's valid
    user.setUsername(username);
    // Stores user as a whole + returns the user
    return storeUser(user);
  }


  /** Get User by ID **/
  public User getUserById(String id) throws Exception {
    JsonDocument doc = bucket.get(id);
    if (doc == null) {
      throw new Exception();
    } else {
      return new User(doc.content());
    }
  }


  /** Get User by Google ID (optional) **/
  public Optional<User> getUserByGoogleId(String googleId) {
    JsonDocument doc = bucket.get(googleId);
    if (doc == null) {
      return Optional.empty();
    } else {
      User.GoogleIdToUser idToUser = new User.GoogleIdToUser(doc.content());
      User user = new User(bucket.get(idToUser.getUserId()).content());
      return Optional.of(user);
    }
  }


  /** Increments (if up == true) and decrements (if up == false) a user's follower count **/
  public boolean incrementFollowerCount(String id, boolean up) throws Exception {
    int amount = up ? 1 : -1;

    N1qlQuery q = N1qlQuery.simple(
      select("numberFollowers").from("`" + DB + "`").where(
        (x(ID).eq(s(id)))
      )
    );

    List<N1qlQueryRow> rows = bucket.query(q).allRows();
    if (rows.size() == 0) {
      return false;
    } else {
      int followers = new User(rows.get(0).value().getObject(DB)).getNumberFollowers();
      N1qlQuery q1 = N1qlQuery.simple(
          "SET numberFollowers=" + (followers + amount) + "FOR u IN `" + DB +
            "`s WHEN u.id='" + id + "'"
      );
      return true;
    }
  }


  /** Increments (if up == true) and decrements (if up == false) a user's following count **/
  public boolean incrementFollowingCount(String id, boolean up) throws Exception {
    int amount = up ? 1 : -1;

    N1qlQuery q = N1qlQuery.simple(
        select("numberFollowings").from("`" + DB + "`").where(
            (x(ID).eq(s(id)))
        )
    );

    List<N1qlQueryRow> rows = bucket.query(q).allRows();
    if(rows.size() == 0) {
      return false;
    }
    else {
      int followings = new User(rows.get(0).value().getObject(DB)).getNumberFollowing();
      N1qlQuery q1 = N1qlQuery.simple(
          "SET numberFollowings="+(followings + amount)+"FOR u IN `" + DB +
            "` WHEN u.id='" + id + "'"
      );
      return true;
    }
  }



}
