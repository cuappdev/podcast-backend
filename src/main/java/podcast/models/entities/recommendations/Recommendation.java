package podcast.models.entities.recommendations;

import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import lombok.Getter;
import podcast.models.entities.Entity;
import podcast.models.entities.podcasts.Episode;
import podcast.models.entities.users.AssociatedUser;
import podcast.models.entities.users.User;
import java.util.Date;
import static podcast.utils.Constants.*;

/**
 * A user's recommendation of a particular
 * podcast episode (a.k.a. 'liking' the episode)
 */
public class Recommendation extends Entity {

  @Getter private Type type = Type.recommendation;
  @Getter private String episodeId;
  @Getter private String seriesTitle;
  @Getter private String title;
  @Getter private String imageUrlSm;
  @Getter private String imageUrlLg;
  @Getter private String audioUrl;
  @Getter private AssociatedUser user;
  @Getter private Date createdAt = new Date();


  /**
   * Constructor from user and episode
   * @param user - User
   * @param episode _ Episode
   */
  public Recommendation(User user, Episode episode) {
    this.episodeId = episode.getId();
    this.seriesTitle = episode.getSeriesTitle();
    this.title = episode.getTitle();
    this.imageUrlSm = episode.getImageUrlSm();
    this.imageUrlLg = episode.getImageUrlLg();
    this.audioUrl = episode.getAudioUrl();
    this.user = new AssociatedUser(user);
  }

  /**
   * Constructor from JsonObject
   * @param object - JsonObject
   */
  public Recommendation(JsonObject object) {
    this.episodeId = object.getString(EPISODE_ID);
    this.seriesTitle = object.getString(SERIES_TITLE);
    this.title = object.getString(TITLE);
    this.imageUrlSm = object.getString(IMAGE_URL_SM);
    this.imageUrlLg = object.getString(IMAGE_URL_LG);
    this.audioUrl = object.getString(AUDIO_URL);
    this.createdAt = new Date(object.getInt(CREATED_AT));
    this.user = new AssociatedUser(object.getObject(USER));
  }

  /** See {@link Entity#toJsonDocument()} */
  public JsonDocument toJsonDocument() {
    return JsonDocument.create(composeKey(this), super.toJsonObject());
  }

  /** Compose a key from episodeId and userId **/
  public static String composeKey(String episodeId, String userId) {
    return Entity.composeKey(String.format("%s:%s", episodeId, userId), Type.recommendation.toString());
  }

  /** Compose a key from episode and user **/
  public static String composeKey(Episode episode, User user) {
    return composeKey(episode.getId(), user.getId());
  }

  /** Compose key from Recommendation **/
  public static String composeKey(Recommendation r) {
    return composeKey(r.getEpisodeId(), r.getUser().getId());
  }

}
