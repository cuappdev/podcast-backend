package podcast.repos;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import podcast.models.entities.followings.Follower;
import podcast.models.entities.followings.Following;
import podcast.models.entities.users.User;
import rx.Observable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.dsl.Expression.*;
import static podcast.utils.Constants.*;

@Component
public class FollowersFollowingsRepo {

  private Bucket bucket;

  @Autowired
  public FollowersFollowingsRepo(@Qualifier("dbBucket") Bucket dbBucket) {
    this.bucket = dbBucket;
  }

  /** Creates a following from user A to B. Also creates a follower from B to A. **/
  public Following storeFollowing(Following following, User owner, User followed) throws Exception {
    Follower follower = new Follower(followed, owner);
    List<JsonDocument> docs = Arrays.asList(
      following.toJsonDocument(),
      follower.toJsonDocument()
    );
    Observable
      .from(docs)
      .flatMap(d -> bucket.async().upsert(d))
      .last()
      .toBlocking()
      .single();
    return following;
  }

  /** Get followers of a user (identified by ownerId) **/
  public List<Follower> getUserFollowers(String ownerId) throws Exception {
    N1qlQuery q = N1qlQuery.simple(
      select("*").from("`" + DB + "`")
      .where(
        (x(TYPE)).eq(s(FOLLOWER))
        .and(x(OWNER_ID).eq(s(ownerId)))
      )
    );

    List<N1qlQueryRow> rows = bucket.query(q).allRows();
    return rows.stream()
      .map(r -> new Follower(r.value().getObject(DB))).collect(Collectors.toList());
  }

  /** Get followings of a user (identified by ownerId) **/
  public List<Following> getUserFollowings(String ownerId) throws Exception {
    N1qlQuery q = N1qlQuery.simple(
      select("*").from("`" + DB + "`")
      .where(
        (x(TYPE).eq(s(FOLLOWING)))
        .and(x(OWNER_ID).eq(s(ownerId)))
      )
    );

    List<N1qlQueryRow> rows = bucket.query(q).allRows();
    return rows.stream()
      .map(r -> new Following(r.value().getObject(DB))).collect(Collectors.toList());
  }

  /** Get following by users **/
  public Optional<Following> getFollowingByUsers(User owner, User followed) {
    JsonDocument doc = bucket.get(Following.composeKey(owner, followed));
    if (doc == null) {
      return Optional.empty();
    } else {
      return Optional.of(new Following(doc.content()));
    }
  }

  /** Delete following (A following B, B's follower A) **/
  public boolean deleteFollowing(Following following, User owner, User followed) throws Exception {
    List<String> keys = Arrays.asList(
      Following.composeKey(following.getOwnerId(), following.getId()),
      Follower.composeKey(following.getId(), following.getOwnerId())
    );
    Observable
      .from(keys)
      .flatMap(x -> bucket.async().remove(x))
      .last()
      .toBlocking()
      .single();
    return true;
  }
}
