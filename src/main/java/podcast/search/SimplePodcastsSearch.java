package podcast.search;

import podcast.models.entities.Episode;
import podcast.models.entities.Podcast;
import podcast.models.entities.Series;
import java.util.List;

/** Podcasts search via indexes on certain fields **/
public class SimplePodcastsSearch extends PodcastsSearch {

  /** {@link PodcastsSearch#searchEpisodes(String)} **/
  public List<Episode> searchEpisodes(String query) {
    // TODO
    return null;
  }

  /** {@link PodcastsSearch#searchSeries(String)} **/
  public List<Series> searchSeries(String query) {
    // TODO
    return null;
  }

  /** {@link PodcastsSearch#searchEverything(String)} **/
  public List<Podcast> searchEverything(String query) {
    // TODO
    return null;
  }

}
