package podcast.search;

import podcast.models.entities.Episode;
import podcast.models.entities.Podcast;
import podcast.models.entities.Series;
import java.util.List;

/** Abstract parent of all podcast search implementations **/
public abstract class PodcastsSearch {

  /** Given a query, search and return resultant episodes **/
  public abstract List<Episode> searchEpisodes(String query, Integer offset, Integer max);

  /** Given a query, search and return resultant series **/
  public abstract List<Series> searchSeries(String query, Integer offset, Integer max);

  /** Given a query, search all podcast info **/
  public abstract List<Podcast> searchEverything(String query, Integer offset, Integer max);

}
