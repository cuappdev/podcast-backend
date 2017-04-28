package podcast.models.entities.podcasts;

import podcast.models.entities.Entity;

/** Abstract parent of podcast entities (series, episodes, etc.) **/
public abstract class Podcast extends Entity {

  protected static String composeKey(Long seriesId, Long pubDate) {
    return String.format("%s:%s", seriesId, pubDate);
  }

}
