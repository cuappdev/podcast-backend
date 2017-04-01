package podcast.search;

import org.springframework.beans.factory.annotation.Autowired;
import podcast.models.entities.*;
import podcast.services.SubscriptionsService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** Abstract parent of all podcast search implementations **/
public abstract class PodcastsSearch {

  protected SubscriptionsService subscriptionsService;

  @Autowired
  public PodcastsSearch(SubscriptionsService subscriptionsService) {
    this.subscriptionsService = subscriptionsService;
  }

  /** Given a query, search and return resultant episodes **/
  public abstract List<Episode> searchEpisodes(String query, Integer offset, Integer max, User user) throws Exception;

  /** Given a query, search and return resultant series **/
  public abstract List<Series> searchSeries(String query, Integer offset, Integer max, User user) throws Exception;

  /** Given a query, search all podcast info **/
  public abstract List<Podcast> searchEverything(String query, Integer offset, Integer max, User user) throws Exception;


  public List<Series> addSubscribed(List<Series> series, User user) throws Exception {
    List<Long> subs = subscriptionsService.getUserSubscriptions(user).stream()
      .map(Subscription::getSeriesId).collect(Collectors.toList());
    Set<Long> subSeriesIdSet = new HashSet<Long>(subs);
    return series.stream().map(s -> {
      s.setIsSubscribed(subSeriesIdSet.contains(s.getId()));
      return s;
    }).collect(Collectors.toList());
  }

}
