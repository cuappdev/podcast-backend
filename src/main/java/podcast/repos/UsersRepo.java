package podcast.repos;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import podcast.models.entities.User;
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

  /* Store a user in the bucket */
  public User storeUser(User user) {
    bucket.upsert(JsonDocument.create(user.getId(), user.toJsonObject()));
    return user;
  }


  /** Get user by username (case insensitive) **/
  public Optional<User> getUserByUsername(String username) {
    username = username.toLowerCase();
    N1qlQuery q = N1qlQuery.simple(
      select("*").from("`" + USERS + "`").where(
        (x("lower(" + USERNAME + ")").eq(s(username)))
      )
    );
    List<N1qlQueryRow> rows = bucket.query(q).allRows();

    // If empty
    if (rows.size() == 0) {
      return Optional.empty();
    }

    // Grab user accordingly
    return Optional.of(new User(rows.get(0).value().getObject(USERS)));
  }


  /** Get User by ID **/
  public User getUserById(String id) throws Exception {
    return new User(bucket.get(id).content());
  }


  /** Get User by Google ID (optional) **/
  public Optional<User> getUserByGoogleId(String googleID) {
    // Prepare and execute N1QL query
    N1qlQuery q = N1qlQuery.simple(
      select("*").from("`" + USERS + "`").where(
        (x(GOOGLE_ID).eq(s(googleID)))
      )
    );
    List<N1qlQueryRow> rows = bucket.query(q).allRows();

    // If empty
    if (rows.size() == 0) {
      return Optional.empty();
    }

    // Grab user accordingly
    return Optional.of(new User(rows.get(0).value().getObject(USERS)));
  }


  /** Remove User by ID (+ return the user just deleted) **/
  public void removeUserById(String id) throws Exception {
    bucket.remove(id);
  }

  /**
   * Increments (if up == true) and decrements (if up == false) a user's follower count
   * @param id
   * @param up
   * @return
   * @throws Exception
   */
  public boolean incrementFollowerCount(String id, boolean up) throws Exception {
    int amount = -1;
    if(up) {
      amount = 1;
    }
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

  /**
   * Increments (if up == true) and decrements (if up == false) a user's following count
   * @param id
   * @return
   * @throws Exception
   */
  public boolean incrementFollowingCount(String id, boolean up) throws Exception {
    int amount = -1;
    if(up) {
      amount = 1;
    }
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
