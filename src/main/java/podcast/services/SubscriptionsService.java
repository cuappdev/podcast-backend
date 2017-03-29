package podcast.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import podcast.models.entities.Series;
import podcast.models.entities.Subscription;
import podcast.models.entities.User;
import podcast.repos.PodcastsRepo;
import podcast.repos.SubscriptionsRepo;
import podcast.repos.UsersRepo;

import java.util.List;

@Service
public class SubscriptionsService {

  private PodcastsRepo podcastsRepo;
  private UsersRepo usersRepo;
  private SubscriptionsRepo subscriptionsRepo;

  @Autowired
  public SubscriptionsService(PodcastsRepo podcastsRepo, SubscriptionsRepo subscriptionsRepo, UsersRepo usersRepo) {
    this.podcastsRepo = podcastsRepo;
    this.subscriptionsRepo = subscriptionsRepo;
  }

  public Subscription createSubscription(User owner, Series series) {
    synchronized (this) {
      Subscription sub = new Subscription(owner, series);
      subscriptionsRepo.storeSubscription(sub);
      return sub;
    }
  }

  public boolean deleteSubscription(Subscription subscription) {
    synchronized (this) {
      return subscriptionsRepo.deleteSubscription(subscription);
    }
  }

  public List<Subscription> getUserSubscriptions(String userId) throws Exception {
    User user = usersRepo.getUserById(userId);
    return subscriptionsRepo.getUserSubscriptions(user);
  }

}
