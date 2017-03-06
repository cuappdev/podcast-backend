package podcast.services;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.couchbase.client.java.query.Statement;
import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.dsl.Expression.*;
import org.springframework.stereotype.Service;
import podcast.models.entities.User;
import java.util.List;
import java.util.Optional;

/**
 * Service to handle storage and querying of
 * user information in Couchbase
 */
@Service
public class UsersService {

  /**
   * Get User by Google ID
   * @param bucket - Bucket
   * @param googleID - String
   * @return - Optional User
   * @throws Exception - if dups
   */
  public Optional<User> getUserByGoogleID(Bucket bucket, String googleID) {
    // Prepare and execute N1QL query
    Statement statement = select("*").where(x("googleID").eq(x("$googleID")));
    JsonObject placeholderValues = JsonObject.create().put("googleID", googleID);
    N1qlQuery q = N1qlQuery.parameterized(statement, placeholderValues);
    List<N1qlQueryRow> rows = bucket.query(q).allRows();

    // If empty
    if (rows.size() == 0) {
      return Optional.empty();
    }

    // This should never happen
    if (rows.size() > 1) {
      // TODO
    }

    // Grab the user accordingly
    return Optional.of(new User(rows.get(0).value()));
  }


}
