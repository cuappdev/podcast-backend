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


  /** Get User by ID **/
  public User getUserById(String id) throws Exception {
    return new User(bucket.get(id).content());
  }


  /** Get User by Google ID (optional) **/
  public Optional<User> getUserByGoogleId(String googleID) {
    /* Prepare and execute N1QL query */
    N1qlQuery q = N1qlQuery.simple("SELECT * FROM users WHERE googleId='" + googleID + "'");
    List<N1qlQueryRow> rows = bucket.query(q).allRows();

    /* If empty */
    if (rows.size() == 0) {
      return Optional.empty();
    }

    /* Grab user accordingly */
    return Optional.of(new User(rows.get(0).value().getObject("users")));
  }


}
