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

import static podcast.models.utils.Constants.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class FollowersFollowingsRepo {
  /* Connection to DB */
  private Bucket bucket;

  private String composeKey(FollowRelationship fr) {
    String endString = "";
    switch(fr.getType()) {
      case FOLLOWER:
        endString = "follower";
        break;
      case FOLLOWING:
        endString = "following";
        break;
      default:
        break;
    }
    return fr.getOwnerId() + ":" + fr.getId() + ":" + endString;
  }

  @Autowired
  public FollowersFollowingsRepo(@Qualifier("followersfollowingsBucket") Bucket followersfollowingsBucket) {
    this.bucket = followersfollowingsBucket;
  }

  /**
   * Creates a following from user A to B. Also creates a follower from B to A.
   * @param following
   * @return
   */
  public Following storeFollowing(Following following, User owner, User followed) {
    Follower follower = new Follower(followed, owner);
    JsonDocument followingDoc = JsonDocument.create(composeKey(following), following.toJsonObject());
    JsonDocument followerDoc = JsonDocument.create(composeKey(follower), follower.toJsonObject());
    bucket.upsert(followingDoc);
    bucket.upsert(followerDoc);
    return following;
  }

  public Optional<List<Follower>> getUserFollowers(String ownerId) {
    N1qlQuery q = N1qlQuery.simple("SELECT * FROM followersfollowings WHERE ownerId='" + ownerId + "' AND type='follower'");
    List<N1qlQueryRow> rows = bucket.query(q).allRows();

    if (rows.size() == 0) {
      return Optional.empty();
    }

    List<Follower> followers = new ArrayList<Follower>();

    for(int i = 0; i < rows.size(); i++) {
      followers.add(new Follower(rows.get(i).value().getObject("followersfollowings")));
    }

    return Optional.of(followers);
  }

  public Optional<List<Following>> getUserFollowings(String ownerId) {
    N1qlQuery q = N1qlQuery.simple("SELECT * FROM followersfollowings WHERE ownerId='" + ownerId + "' AND type='following'");
    List<N1qlQueryRow> rows = bucket.query(q).allRows();

    if (rows.size() == 0) {
      return Optional.empty();
    }

    List<Following> followings = new ArrayList<Following>();

    for(int i = 0; i < rows.size(); i++) {
      followings.add(new Following(rows.get(i).value().getObject("followersfollowings")));
    }

    return Optional.of(followings);
  }

  public Optional<Following> getFollowingByUsers(User owner, User followed) {
    N1qlQuery q = N1qlQuery.simple("SELECT * FROM followersfollowings WHERE ownerId='" +
        owner.getId() + " AND id='" + followed.getId() + " AND type='following'");
    List<N1qlQueryRow> rows = bucket.query(q).allRows();

    // TODO exception handling

    return Optional.of(new Following(rows.get(0).value().getObject("followersfollowings")));
  }

  public boolean deleteFollowing(Following following) {
    /* Can we do this without n1ql queries? */

    String qs1 = "DELETE * FROM followersfollowings WHERE ownerId='" +
        following.getOwnerId() + " AND id='" + following.getId() + " AND type='following'";
    N1qlQuery q1 = N1qlQuery.simple(qs1);
    bucket.query(q1);

    String qs2 = "DELETE * FROM followersfollowings WHERE ownerId='" +
        following.getId() + " AND id='" + following.getOwnerId() + " AND type='follower'";
    N1qlQuery q2 = N1qlQuery.simple(qs2);
    bucket.query(q2);

    // TODO Better exception handling for the above operations

    return true;
  }
}
