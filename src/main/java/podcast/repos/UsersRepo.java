package podcast.repos;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.error.DocumentDoesNotExistException;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import podcast.models.entities.User;
import rx.Observable;
import rx.functions.Func1;
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
  public UsersRepo(@Qualifier("usersBucket") Bucket usersBucket) {
    this.bucket = usersBucket;
  }


  /** Store a user in the bucket **/
  public User storeUser(User user) {
    // Add docs for batch insert
    List<JsonDocument> docs = user.docs();
    // Insert them in one batch, waiting until the last one is done
    Observable
      .from(docs)
      .flatMap(new Func1<JsonDocument, Observable<?>>() {
        @Override
        public Observable<?> call(JsonDocument jsonDocument) {
          return bucket.async().upsert(jsonDocument);
        }
      })
      .last()
      .toBlocking()
      .single();

    return user;
  }


  /** Checks to see if this username is available **/
  public boolean usernameAvailable(String username) {
    try {
      User.UsernameToUser usernameToUser = new User.UsernameToUser(bucket.get(username).content());
      return false;
    } catch (DocumentDoesNotExistException e) {
      return true;
    }
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
    return new User(bucket.get(id).content());
  }


  /** Get User by Google ID (optional) **/
  public Optional<User> getUserByGoogleId(String googleID) {
    try {
      User.GoogleIdToUser idToUser = new User.GoogleIdToUser(bucket.get(googleID).content());
      User user = new User(bucket.get(idToUser.getUserId()).content());
      return Optional.of(user);
    } catch (Exception e) {
      e.printStackTrace();
      return Optional.empty();
    }
  }


  /** Remove User by ID (+ return the user just deleted) **/
  public void removeUserById(String id) throws Exception {
    User user = new User(bucket.get(id).content());
    // TODO - remove all the lookup entities of the user too
    // TODO - remove traces of the user in other parts of the DB
  }


  /** Increments (if up == true) and decrements (if up == false) a user's follower count **/
  public boolean incrementFollowerCount(String id, boolean up) throws Exception {
    int amount = up ? 1 : -1;

    N1qlQuery q = N1qlQuery.simple(
      select("numberFollowers").from("`" + USERS + "`").where(
        (x(ID).eq(s(id)))
      )
    );

    List<N1qlQueryRow> rows = bucket.query(q).allRows();
    if (rows.size() == 0) {
      return false;
    } else {
      int followers = new User(rows.get(0).value().getObject(USERS)).getNumberFollowers();
      N1qlQuery q1 = N1qlQuery.simple(
          "SET numberFollowers=" + (followers + amount) + "FOR u IN users WHEN u.id='" + id + "'"
      );
      return true;
    }
  }


  /** Increments (if up == true) and decrements (if up == false) a user's following count **/
  public boolean incrementFollowingCount(String id, boolean up) throws Exception {
    int amount = up ? 1 : -1;

    N1qlQuery q = N1qlQuery.simple(
        select("numberFollowings").from("`" + USERS + "`").where(
            (x(ID).eq(s(id)))
        )
    );

    List<N1qlQueryRow> rows = bucket.query(q).allRows();
    if(rows.size() == 0) {
      return false;
    }
    else {
      int followings = new User(rows.get(0).value().getObject(USERS)).getNumberFollowing();
      N1qlQuery q1 = N1qlQuery.simple(
          "SET numberFollowings="+(followings + amount)+"FOR u IN users WHEN u.id='"+id+"'"
      );
      return true;
    }
  }



}
