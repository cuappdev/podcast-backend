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
  private FollowersFollowingsRepo ffRepo;

  @Autowired
  public FollowersFollowingsService(UsersRepo usersRepo, FollowersFollowingsRepo ffRepo) {
    this.usersRepo = usersRepo;
  }

  public Following createFollowing(User owner, String followedId) {
    try {
      User followed = usersRepo.getUserById(followedId);
      Following following = new Following(owner, followed);
      ffRepo.storeFollowing(following, owner, followed);
      return following;
    }
    catch(Exception e) {
      e.printStackTrace(); // TODO figure out what goes here
    }
    finally {
      return null;
    }
  }

  public boolean deleteFollowing(User owner, String followedId) {
    try {
      User followed = usersRepo.getUserById(followedId);
      Optional<Following> followingOpt = ffRepo.getFollowingByUsers(owner, followed);
      Following following = followingOpt.orElse(null);
      if(following != null) {
        return ffRepo.deleteFollowing(following);
      }
      else {
        throw new Exception("Can't delete nonexistent following."); // TODO better exception handling
      }
    }
    catch(Exception e) {
      e.printStackTrace(); // TODO figure out what goes here
    }
    finally {
      return false;
    }
  }

  public Optional<List<Following>> getUserFollowings(String ownerId) {
    try {
      Optional<List<Following>> followings = ffRepo.getUserFollowings(ownerId);
      return followings;
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    finally {
      return null;
    }
  }

  public Optional<List<Follower>> getUserFollowers(String ownerId) {
    try {
      Optional<List<Follower>> followers = ffRepo.getUserFollowers(ownerId);
      return followers;
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    finally {
      return null;
    }
  }

}
