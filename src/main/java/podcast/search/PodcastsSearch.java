package podcast.search;

import org.springframework.beans.factory.annotation.Autowired;
import podcast.models.entities.podcasts.Episode;
import podcast.models.entities.podcasts.Podcast;
import podcast.models.entities.podcasts.Series;
import podcast.models.entities.subscriptions.Subscription;
import podcast.models.entities.users.User;
import podcast.services.PodcastsService;
import podcast.services.SubscriptionsService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** Abstract parent of all podcast search implementations **/
public abstract class PodcastsSearch {

  /** Given a query, search and return resultant episodes **/
  public abstract PodcastsService.EpisodesInfo searchEpisodes(String query, Integer offset, Integer max, User user) throws Exception;

  /** Given a query, search and return resultant series **/
  public abstract PodcastsService.SeriesInfo searchSeries(String query, Integer offset, Integer max, User user) throws Exception;

}
