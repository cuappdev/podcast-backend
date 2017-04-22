package podcast.models.entities.podcasts;

import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import lombok.Getter;
import podcast.models.entities.Entity;
import podcast.models.entities.podcasts.Podcast;

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
  @Getter private Integer numberRecommenders;
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
    this.pubDate = object.getLong(PUB_DATE);
    this.duration = object.getString(DURATION);
    this.audioUrl = object.getString(AUDIO_URL);
    this.numberRecommenders = object.getInt(NUMBER_RECOMMENDERS);
    this.tags = object.getArray(TAGS) == null ? new ArrayList<String>() : object.getArray(TAGS).toList()
      .stream().map(o -> ((String) o)).collect(Collectors.toList());
  }

  /** Establishes a UUID for episodes for client **/
  public String getId() {
    return String.format("%s:%s", getSeriesId(), getPubDate());
  }

  /** Increment the number of people who have recommended this */
  public void incrementNumberRecommenders() {
    numberRecommenders += 1;
  }

  /** Decrement the number of people who have recommended this */
  public void decrementNumberRecommenders() {
    numberRecommenders -= 1;
  }

  /** Grab these components -> return the pair **/
  public static CompositeEpisodeKey getSeriesIdAndPubDate(String episodeId) {
    String[] split = episodeId.split(":");
    assert split.length == 2;
    return new CompositeEpisodeKey(Long.parseLong(split[0]), Long.parseLong(split[1]));
  }

  /** Basic class that holds seriesId / pubDate info */
  public static class CompositeEpisodeKey {
    @Getter Long seriesId;
    @Getter Long pubDate;
    private CompositeEpisodeKey (Long seriesId, Long pubDate) {
      this.seriesId = seriesId;
      this.pubDate = pubDate;
    }
  }

  /** See {@link Entity#toJsonDocument()} */
  public JsonDocument toJsonDocument() {
    JsonObject object = super.toJsonObject();
    return JsonDocument.create(composeKey(seriesId, pubDate), object);
  }

  /** When an episode does not exist **/
  public static class EpisodeDoesNotExistException extends Exception {
    public EpisodeDoesNotExistException() {
      super("Episode does not exist");
    }
  }
}
