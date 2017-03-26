package podcast.models.entities;

/** Abstract parent of podcast entities (series, episodes, etc.) **/
public abstract class Podcast {

  public static String composeKey(Long seriesId, Long timestamp) {
    return "" + seriesId + ":" + timestamp;
  }

}
