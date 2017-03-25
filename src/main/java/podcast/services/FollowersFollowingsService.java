package podcast.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import podcast.models.entities.Follower;
import podcast.models.entities.Following;
import podcast.models.entities.User;
import podcast.repos.FollowersFollowingsRepo;
import podcast.repos.UsersRepo;

import java.util.List;
import java.util.Optional;

@Service
public class FollowersFollowingsService {

  /* Database communication */
  private UsersRepo usersRepo;
  private FollowersFollowingsRepo followersFollowingsRepo;

  @Autowired
  public FollowersFollowingsService(UsersRepo usersRepo, FollowersFollowingsRepo followersFollowingsRepo) {
    this.usersRepo = usersRepo;
    this.followersFollowingsRepo = followersFollowingsRepo;
  }

  public Following createFollowing(User owner, String followedId) {
    try {
      User followed = usersRepo.getUserById(followedId);
      Following following = new Following(owner, followed);
      followersFollowingsRepo.storeFollowing(following, owner, followed);
      usersRepo.incrementFollowerCount(followedId, true);
      usersRepo.incrementFollowingCount(owner.getId(), true);
      return following;
    }
    catch(Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public boolean deleteFollowing(User owner, String followedId) {
    try {
      User followed = usersRepo.getUserById(followedId);
      Optional<Following> followingOpt = followersFollowingsRepo.getFollowingByUsers(owner, followed);
      Following following = followingOpt.orElse(null);
      usersRepo.incrementFollowerCount(followedId, false);
      usersRepo.incrementFollowingCount(owner.getId(), false);
      if(following != null) {
        return followersFollowingsRepo.deleteFollowing(following);
      }
      else {
        throw new Exception("Can't delete nonexistent following."); // TODO better exception handling
      }
    }
    catch(Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  public Optional<List<Following>> getUserFollowings(String ownerId) {
    try {
      return followersFollowingsRepo.getUserFollowings(ownerId);
    }
    catch(Exception e) {
      e.printStackTrace();
      return Optional.empty();
    }
  }

  public Optional<List<Follower>> getUserFollowers(String ownerId) {
    try {
      return followersFollowingsRepo.getUserFollowers(ownerId);
    }
    catch(Exception e) {
      e.printStackTrace();
      return Optional.empty();
    }
  }

}
