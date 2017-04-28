package podcast.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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

  private final ApplicationEventPublisher publisher;
  private final UsersRepo usersRepo;
  private final FollowersFollowingsRepo followersFollowingsRepo;


  @Autowired
  public FollowersFollowingsService(ApplicationEventPublisher publisher,
                                    UsersRepo usersRepo,
                                    FollowersFollowingsRepo followersFollowingsRepo) {
    this.publisher = publisher;
    this.usersRepo = usersRepo;
    this.followersFollowingsRepo = followersFollowingsRepo;
  }

  /** Create a following **/
  public Following createFollowing(User owner, String followedId) throws Exception {
    if (owner.getId().equals(followedId)) {
      throw new FollowRelationship.FollowingSelfException();
    }
    User followed = usersRepo.getUserById(followedId);
    Following following = new Following(owner, followed);
    publisher.publishEvent(new FollowingCreationEvent(following, owner, followed));
    return followersFollowingsRepo.storeFollowing(following, owner, followed);
  }

  /** Delete a following **/
  public boolean deleteFollowing(User owner, String followedId) throws Exception {
    User followed = usersRepo.getUserById(followedId);
    Optional<Following> followingOpt = followersFollowingsRepo.getFollowingByUsers(owner, followed);
    if (!followingOpt.isPresent()) {
      throw new FollowRelationship.NonExistentFollowingException();
    }
    publisher.publishEvent(new FollowingDeletionEvent(followingOpt.get(), owner, followed));
    return followersFollowingsRepo.deleteFollowing(followingOpt.get(), owner, followed);
  }

  /** Get a user's followings by ownerId **/
  public List<Following> getUserFollowings(String ownerId) throws Exception {
    return followersFollowingsRepo.getUserFollowings(ownerId);
  }

  /** Get a user's followers by ownerId **/
  public List<Follower> getUserFollowers(String ownerId) throws Exception {
    return followersFollowingsRepo.getUserFollowers(ownerId);
  }

  // MARK - events

  private static abstract class FollowingEvent {
    Following following;
    User owner;
    User followed;

    /** Constructor */
    public FollowingEvent(Following following,
                          User owner,
                          User followed) {
      this.following = following;
      this.owner = owner;
      this.followed = followed;
    }
  }

  static class FollowingCreationEvent extends FollowingEvent {
    private FollowingCreationEvent(Following following,
                                   User owner,
                                   User followed) {
      super(following, owner, followed);
    }
  }

  static class FollowingDeletionEvent extends FollowingEvent {
    private FollowingDeletionEvent(Following following,
                                   User owner,
                                   User followed) {
      super(following, owner, followed);
    }
  }
}
