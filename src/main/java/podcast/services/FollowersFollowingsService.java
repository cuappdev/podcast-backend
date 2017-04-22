package podcast.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import podcast.models.entities.followings.FollowRelationship;
import podcast.models.entities.followings.Follower;
import podcast.models.entities.followings.Following;
import podcast.models.entities.users.User;
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


  /** Atomically create a following and update all other DS's in the DB **/
  public Following createFollowing(User owner, String followedId) throws Exception {
    synchronized (this) {
      User followed = usersRepo.getUserById(followedId);
      Following following = new Following(owner, followed);
      followersFollowingsRepo.storeFollowing(following, owner, followed);
      return following;
    }
  }


  /** Atomically delete a following and update all other DS's in the DB **/
  public boolean deleteFollowing(User owner, String followedId) throws Exception {
    synchronized (this) {
      User followed = usersRepo.getUserById(followedId);
      Optional<Following> followingOpt = followersFollowingsRepo.getFollowingByUsers(owner, followed);
      if (!followingOpt.isPresent()) {
        throw new FollowRelationship.NonExistentFollowingException();
      }
      return followersFollowingsRepo.deleteFollowing(followingOpt.get(), owner, followed);
    }
  }

  /** Get a user's followings by ownerId **/
  public List<Following> getUserFollowings(String ownerId) throws Exception {
    return followersFollowingsRepo.getUserFollowings(ownerId);
  }


  /** Get a user's followers by ownerId **/
  public List<Follower> getUserFollowers(String ownerId) throws Exception {
    return followersFollowingsRepo.getUserFollowers(ownerId);
  }

}
