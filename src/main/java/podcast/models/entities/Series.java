package podcast.models.entities;

import com.couchbase.client.java.document.json.JsonObject;
import lombok.Getter;
import podcast.models.utils.Constants;
import java.util.List;
import java.util.stream.Collectors;
import static podcast.models.utils.Constants.*;

/**
 * Podcast series (e.g. 'Serial' itself)
 */
public class Series extends Podcast {

  @Getter private Type type = Type.SERIES;
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
    this.genres = object.getArray(GENRES).toList()
      .stream().map(o -> { return (String) o; }).collect(Collectors.toList());
  }


}
