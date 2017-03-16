package podcast.models.entities;

import com.couchbase.client.java.document.json.JsonObject;
import lombok.Getter;

import java.util.Date;

import static podcast.utils.Constants.*;

/**
 * A user's recommendation of a particular
 * podcast episode (a.k.a. 'liking' the episode)
 */
public class Recommendation extends Entity {

  @Getter private Type type = Type.RECOMMENDATION;
  @Getter private String seriesTitle;
  @Getter private String title;
  @Getter private String imageUrlSm;
  @Getter private String imageUrlLg;
  @Getter private Person user;
  @Getter private Date createdAt = new Date();


  /**
   * Constructor from user and episode
   * @param user - User
   * @param episode _ Episode
   */
  public Recommendation(User user, Episode episode) {
    this.seriesTitle = episode.getSeriesTitle();
    this.title = episode.getTitle();
    this.imageUrlSm = episode.getImageUrlSm();
    this.imageUrlLg = episode.getImageUrlLg();
    this.user = new Person(user);
  }


  /**
   * Constructor from JsonObject
   * @param object - JsonObject
   */
  public Recommendation(JsonObject object) {
    this.seriesTitle = object.getString(SERIES_TITLE);
    this.title = object.getString(TITLE);
    this.imageUrlSm = object.getString(IMAGE_URL_SM);
    this.imageUrlLg = object.getString(IMAGE_URL_LG);
    this.user = new Person(object.getObject(USER));
  }


  /**
   * See {@link Entity#toJsonObject()}
   */
  public JsonObject toJsonObject() {
    return JsonObject.create()
      .put(TYPE, type)
      .put(SERIES_TITLE, seriesTitle)
      .put(TITLE, title)
      .put(IMAGE_URL_SM, imageUrlSm)
      .put(IMAGE_URL_LG, imageUrlLg)
      .put(USER, user)
      .put(CREATED_AT, createdAt);
  }

}
