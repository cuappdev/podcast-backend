package podcast.models.entities;

/** Abstract parent of podcast entities (series, episodes, etc.) **/
public abstract class Podcast extends Entity {

  public static String composeKey(Long seriesId, Long pubDate) {
    return String.format("%s:%s", seriesId, pubDate);
  }

}
