package podcast.search;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryRow;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import podcast.models.entities.User;
import java.util.List;
import java.util.stream.Collectors;
import static podcast.models.utils.Constants.*;

/** Users search via indexes on certain fields **/
@Component
@Qualifier("simpleUsersSearch")
public class SimpleUsersSearch extends UsersSearch {

  private Bucket bucket;

  public SimpleUsersSearch(@Qualifier("usersBucket") Bucket usersBucket) {
    this.bucket = usersBucket;
  }

  /** {@link UsersSearch#searchUsers(String, Integer, Integer)} **/
  public List<User> searchUsers(String query, Integer pageSize, Integer page) {
    query = query.trim(); // cleanse query
    String queryString =
      "SELECT * FROM " + USERS + " WHERE " +
        USERNAME + " LIKE '" + query + "%' OR " +
        FIRST_NAME + " LIKE '" + query + "%' OR " +
        LAST_NAME + " LIKE '" + query + "%'";
    N1qlQuery q = N1qlQuery.simple(queryString);
    List<N1qlQueryRow> rows = bucket.query(q).allRows();

    return rows.stream()
      .map(r -> { return new User(r.value().getObject(USERS)); })
      .collect(Collectors.toList());
  }

}
