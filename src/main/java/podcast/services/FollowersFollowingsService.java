package podcast.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import podcast.models.entities.Following;
import podcast.models.entities.User;
import podcast.repos.FollowersFollowingsRepo;
import podcast.repos.UsersRepo;

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
      Following following = ffRepo.getFollowingByUsers(owner, followed);
      return ffRepo.deleteFollowing(following);
    }
    catch(Exception e) {
      e.printStackTrace(); // TODO figure out what goes here
    }
    finally {
      return false;
    }
  }

}
