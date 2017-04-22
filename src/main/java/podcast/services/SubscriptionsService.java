package podcast.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import podcast.models.entities.podcasts.Series;
import podcast.models.entities.subscriptions.Subscription;
import podcast.models.entities.users.User;
import podcast.repos.PodcastsRepo;
import podcast.repos.SubscriptionsRepo;
import podcast.repos.UsersRepo;
import java.util.List;

@Service
public class SubscriptionsService {

  private final ApplicationEventPublisher publisher;
  private final PodcastsRepo podcastsRepo;
  private final UsersRepo usersRepo;
  private final SubscriptionsRepo subscriptionsRepo;

  @Autowired
  public SubscriptionsService(ApplicationEventPublisher publisher,
                              PodcastsRepo podcastsRepo,
                              SubscriptionsRepo subscriptionsRepo,
                              UsersRepo usersRepo) {
    this.publisher = publisher;
    this.podcastsRepo = podcastsRepo;
    this.subscriptionsRepo = subscriptionsRepo;
    this.usersRepo = usersRepo;
  }

  /** Create a subscription and broadcast the creation event */
  public Subscription createSubscription(User owner, Long seriesId) {
    Series series = podcastsRepo.getSeries(seriesId);
    Subscription subscription = new Subscription(owner, series);
    publisher.publishEvent(new SubscriptionCreationEvent(subscription, series, owner));
    return subscriptionsRepo.storeSubscription(subscription);
  }

  /** Delete a subscription and broadcast the deletion event */
  public Subscription deleteSubscription(User owner, Long seriesId) {
    Series series = podcastsRepo.getSeries(seriesId);
    Subscription subscription = subscriptionsRepo.getSubscription(owner, seriesId);
    publisher.publishEvent(new SubscriptionDeletionEvent(subscription, series, owner));
    return subscriptionsRepo.deleteSubscription(subscription);
  }

  /** Get a user's subscriptions by the user's ID */
  public List<Subscription> getUserSubscriptions(String userId) throws Exception {
    User user = usersRepo.getUserById(userId);
    return getUserSubscriptions(user);
  }

  /** Get a user's subscriptions by the user */
  public List<Subscription> getUserSubscriptions(User user) throws Exception {
    return subscriptionsRepo.getUserSubscriptions(user);
  }


  // MARK - events

  private static abstract class SubscriptionEvent {
    Subscription subscription;
    Series series;
    User user;

    /** Constructor */
    protected SubscriptionEvent(Subscription subscription,
                                Series series,
                                User user) {
      this.subscription = subscription;
      this.series = series;
      this.user = user;
    }
  }

  static class SubscriptionCreationEvent extends SubscriptionEvent {
    private SubscriptionCreationEvent(Subscription subscription,
                                      Series series,
                                      User user) {
      super(subscription, series, user);
    }
  }

  static class SubscriptionDeletionEvent extends SubscriptionEvent {
    private SubscriptionDeletionEvent(Subscription subscription,
                                      Series series,
                                      User user) {
      super(subscription, series, user);
    }
  }

}
