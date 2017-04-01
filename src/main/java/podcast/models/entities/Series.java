package podcast.models.entities;

import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import lombok.Getter;
import lombok.Setter;

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
  @Getter @Setter private boolean isSubscribed = false;


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

  public static class SeriesDoesNotExistException extends Exception {
    public SeriesDoesNotExistException() { super("Series does not exist"); }
  }

  public void incrementSubscriberCount() {
    numberSubscribers += 1;
  }

  public void decrementSubscriberCount() {
    numberSubscribers -= 1;
  }

  public JsonDocument toJsonDocument() {
    return JsonDocument.create(composeKey(id, SERIES_PUB_DATE), super.toJsonObject());
  }



}
