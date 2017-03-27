package podcast.models.entities;

import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import lombok.Getter;
import java.util.Date;
import static podcast.utils.Constants.*;

/**
 * A user's subscription ('following') of a podcast series.
 */
public class Subscription extends Entity {

  @Getter private Type type = Type.subscription;
  @Getter private String seriesTitle;
  @Getter private Long seriesId;
  @Getter private String imageUrlSm;
  @Getter private String imageUrlLg;
  @Getter private Person user;
  @Getter private Date createdAt = new Date();


  /**
   * Constructor from user and series
   * @param user - User
   * @param series - Episode
   */
  public Subscription(User user, Series series) {
    this.seriesTitle = series.getTitle();
    this.seriesId = series.getId();
    this.imageUrlSm = series.getImageUrlSm();
    this.imageUrlLg = series.getImageUrlLg();
    this.user = new Person(user);
  }


  /** Constructor from JsonObject **/
  public Subscription(JsonObject object) {
    this.seriesTitle = object.getString(SERIES_TITLE);
    this.seriesId = object.getLong(SERIES_ID);
    this.imageUrlSm = object.getString(IMAGE_URL_SM);
    this.imageUrlLg = object.getString(IMAGE_URL_LG);
    this.user = new Person(object.getObject(USER));
  }

  /** See {@link Entity#toJsonDocument()} **/
  public JsonDocument toJsonDocument() {
    return JsonDocument.create(composeKey(this), super.toJsonObject());
  }


  /** Compose Key from Subscription **/
  public static String composeKey(Subscription s) {
    return String.format("%s:%s:%s", s.getSeriesTitle(), s.getUser().getId(), Type.SUBSCRIPTION.toString());
  }


}
