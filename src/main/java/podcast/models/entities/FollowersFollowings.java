package podcast.models.entities;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import com.fasterxml.uuid.Generators;
import lombok.Getter;

public class FollowersFollowings extends Entity {
    /** Represents a follower-following relationship owned by a user.
        Note that followers and followings may not contain duplicates. **/
    @Getter private UUID uuid;
    @Getter private List<UUID> followers;
    @Getter private List<UUID> followings;

    public FollowersFollowings() {
        this.uuid = Generators.timeBasedGenerator().generate();
        this.followers = new ArrayList<UUID>();
        this.followings = new ArrayList<UUID>();
    }

    public boolean addFollower(UUID followerUUID) {
        if(!(this.followers.contains(followerUUID))) {
            return this.followers.add(followerUUID);
        }
        return false;
    }

    public boolean removeFollower(UUID followerUUID) {
        return this.followers.remove(followerUUID);
    }

    public boolean addFollowing(UUID followingUUID) {
        if(!(this.followers.contains(followingUUID))) {
            return this.followings.add(followingUUID);
        }
        return false;
    }

    public boolean removeFollowing(UUID followingUUID) {
        return this.followers.remove(followingUUID);
    }

    public JsonObject toJsonObject() {
        // TODO
        return null;
    }
}
