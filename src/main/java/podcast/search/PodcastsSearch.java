package podcast.search;

import podcast.models.entities.podcasts.Episode;
import podcast.models.entities.podcasts.Series;
import java.util.List;

/** Abstract parent of all podcast search implementations **/
public abstract class PodcastsSearch {

  /** Given a query, search and return resultant episodes **/
  public abstract List<Episode> searchEpisodes(String query, Integer offset, Integer max) throws Exception;

  /** Given a query, search and return resultant series **/
  public abstract List<Series> searchSeries(String query, Integer offset, Integer max) throws Exception;

}
