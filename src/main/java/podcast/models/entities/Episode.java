package podcast.models.entities;

import com.couchbase.client.java.document.json.JsonObject;
import lombok.Getter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import static podcast.utils.Constants.*;

/**
 * Podcast episode (e.g. an episode of 'Serial')
 */
public class Episode extends Podcast {

  @Getter private Type type = Type.episode;
  @Getter private Long seriesId;
  @Getter private String seriesTitle;
  @Getter private String imageUrlSm;
  @Getter private String imageUrlLg;
  @Getter private String title;
  @Getter private String author;
  @Getter private String summary;
  @Getter private Long pubDate;
  @Getter private String duration;
  @Getter private String audioUrl;
  @Getter private List<String> tags;


  /**
   * Constructor from Couchbase JsonObject
   * @param object - JsonObject
   */
  public Episode(JsonObject object) {
    this.seriesId = object.getLong(SERIES_ID);
    this.seriesTitle = object.getString(SERIES_TITLE);
    this.imageUrlSm = object.getString(IMAGE_URL_SM);
    this.imageUrlLg = object.getString(IMAGE_URL_LG);
    this.title = object.getString(TITLE);
    this.author = object.getString(AUTHOR);
    this.summary = object.getString(SUMMARY);
    this.pubDate = object.getLong(PUB_DATE) == null ? null : object.getLong(PUB_DATE);
    this.duration = object.getString(DURATION);
    this.audioUrl = object.getString(AUDIO_URL);
    this.tags = object.getArray(TAGS) == null ? new ArrayList<String>() : object.getArray(TAGS).toList()
      .stream().map(o -> { return (String) o; }).collect(Collectors.toList());
  }

  /** Establishes a UUID for episodes for client **/
  public String getId() {
    return String.format("%s:%s", getSeriesId(), getPubDate());
  }

  /** Grab these components -> return the pair **/
  public static CompositeEpisodeKey getSeriesIdAndPubDate(String episodeId) {
    String[] split = episodeId.split(":");
    assert split.length == 2;
    return new CompositeEpisodeKey(Long.parseLong(split[0]), Long.parseLong(split[1]));
  }

  public static class CompositeEpisodeKey {
    @Getter Long seriesId;
    @Getter Long pubDate;
    private CompositeEpisodeKey (Long seriesId, Long pubDate) {
      this.seriesId = seriesId;
      this.pubDate = pubDate;
    }
  }


}
