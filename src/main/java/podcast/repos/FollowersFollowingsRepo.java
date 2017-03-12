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

  /* Store a user in the bucket */
  public FollowRelationship storeFollowRelationship(FollowRelationship fr) {
    bucket.upsert(JsonDocument.create(composeKey(fr), fr.toJsonObject()));
    return fr;
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
}
