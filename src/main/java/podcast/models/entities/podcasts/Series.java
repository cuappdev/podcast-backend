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
 * Podcast series (e.g. 'Serial' itself)
 */
public class Series extends Podcast {

  @Getter private Type type = Type.series;
  @Getter private Long id;
  @Getter private String title;
  @Getter private String country;
  @Getter private String author;
  @Getter private String imageUrlSm;
  @Getter private String imageUrlLg;
  @Getter private String feedUrl;
  @Getter private Integer numberSubscribers;
  @Getter private List<String> genres;

  /**
   * Constructor from Couchbase JsonObject
   * @param object - JsonObject
   */
  public Series(JsonObject object) {
    this.id = object.getLong(ID);
    this.title = object.getString(TITLE);
    this.country = object.getString(COUNTRY);
    this.author = object.getString(AUTHOR);
    this.imageUrlSm = object.getString(IMAGE_URL_SM);
    this.imageUrlLg = object.getString(IMAGE_URL_LG);
    this.feedUrl = object.getString(FEED_URL);
    this.numberSubscribers = object.getInt(NUMBER_SUBSCRIBERS) == null ? 0 : object.getInt(NUMBER_SUBSCRIBERS);
    this.genres = object.getArray(GENRES) == null ? new ArrayList<String>() : object.getArray(GENRES).toList()
      .stream().map(o -> { return (String) o; }).collect(Collectors.toList());
  }

  /** Increment the number of people who have subscribed to this.
   * Static b/c original JsonDocument returned in order to ensure CAS value is unchanged */
  public static JsonDocument incrementSubscriberCount(JsonDocument doc) {
    assert doc.content().getString(TYPE).equals(SERIES);
    Integer originalNum = doc.content().getInt(NUMBER_SUBSCRIBERS) != null ?
      doc.content().getInt(NUMBER_SUBSCRIBERS) : 0;
    doc.content().put(NUMBER_SUBSCRIBERS, originalNum + 1);
    return doc;
  }

  /** Decrement the number of people who have subscribed to this.
   * Static b/c original JsonDocument returned in order to ensure CAS value is unchanged */
  public static JsonDocument decrementSubscriberCount(JsonDocument doc) {
    assert doc.content().getString(TYPE).equals(SERIES);
    Integer originalNum = doc.content().getInt(NUMBER_SUBSCRIBERS) != null ?
      doc.content().getInt(NUMBER_SUBSCRIBERS) : 0;
    doc.content().put(NUMBER_SUBSCRIBERS, originalNum - 1);
    return doc;
  }

  /** Compose series key **/
  public static String composeKey(Long seriesId) {
    return Podcast.composeKey(seriesId, SERIES_PUB_DATE);
  }

  /** See {@link Entity#toJsonDocument()} */
  public JsonDocument toJsonDocument() {
    JsonObject object = super.toJsonObject();
    return JsonDocument.create(composeKey(id, SERIES_PUB_DATE), object);
  }

  /** When the series does not exist */
  public static class SeriesDoesNotExistException extends Exception {
    public SeriesDoesNotExistException() { super("Series does not exist"); }
  }
}
