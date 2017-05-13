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
    this.genres = object.getArray(GENRES) == null ? new ArrayList<String>() : object.getArray(GENRES).toList()
      .stream().map(o -> { return (String) o; }).collect(Collectors.toList());
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
