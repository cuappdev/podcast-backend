package podcast.repos;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import podcast.models.entities.FollowRelationship;
import podcast.models.entities.Follower;
import podcast.models.entities.Following;
import podcast.models.entities.User;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.dsl.Expression.*;
import static podcast.utils.Constants.*;

@Component
public class FollowersFollowingsRepo {

  /* Connection to DB */
  private Bucket bucket;

  @Autowired
  public FollowersFollowingsRepo(@Qualifier("dbBucket") Bucket dbBucket) {
    this.bucket = dbBucket;
  }

  /** Creates a following from user A to B. Also creates a follower from B to A. **/
  public Following storeFollowing(Following following, User owner, User followed) {
    Follower follower = new Follower(followed, owner);
    JsonDocument followingDoc = following.toJsonDocument();
    JsonDocument followerDoc = follower.toJsonDocument();
    bucket.upsert(followingDoc);
    bucket.upsert(followerDoc);
    return following;
  }


  /** Get followers of a user (identified by ownerId) **/
  public Optional<List<Follower>> getUserFollowers(String ownerId) {
    N1qlQuery q = N1qlQuery.simple(
      select("*").from("`" + DB + "`")
      .where(
        (x(TYPE)).eq(s(FOLLOWER))
        .and(x(OWNER_ID).eq(s(ownerId)))
        .and(x(TYPE).eq(s(FOLLOWER)))
      )
    );

    List<N1qlQueryRow> rows = bucket.query(q).allRows();

    if (rows.size() == 0) {
      return Optional.empty();
    }

    return Optional.of(
      rows.stream()
        .map(r -> new Follower(r.value().getObject(DB))).collect(Collectors.toList())
    );
  }


  /** Get followings of a user (identified by ownerId) **/
  public Optional<List<Following>> getUserFollowings(String ownerId) {
    N1qlQuery q = N1qlQuery.simple(
      select("*").from("`" + DB + "`")
      .where(
        (x(TYPE).eq(s(FOLLOWING)))
        .and(x(OWNER_ID).eq(s(ownerId)))
        .and(x(TYPE).eq(s(FOLLOWING)))
      )
    );

    List<N1qlQueryRow> rows = bucket.query(q).allRows();

    if (rows.size() == 0) {
      return Optional.empty();
    }

    return Optional.of(
      rows.stream()
        .map(r -> new Following(r.value().getObject(DB))).collect(Collectors.toList())
    );
  }


  /** Get following by users **/
  public Optional<Following> getFollowingByUsers(User owner, User followed) {
    try {
      return Optional.of(
        new Following(bucket.get(FollowRelationship.composeKey(owner, followed, Type.FOLLOWING)).content()));
    } catch (Exception e) {
      return Optional.empty();
    }
  }


  /** Delete following (A following B, B's follower A) **/
  public boolean deleteFollowing(Following following) {
    try {
      bucket.remove(FollowRelationship.composeKey(following.getOwnerId(), following.getId(), Type.FOLLOWING));
      bucket.remove(FollowRelationship.composeKey(following.getId(), following.getOwnerId(), Type.FOLLOWER));
      return true;
    } catch (Exception e) {
      return false;
    }
  }


}
