package podcast.models.entities.history;

import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import lombok.Getter;
import podcast.models.entities.Entity;
import podcast.models.entities.podcasts.Episode;
import podcast.models.entities.users.User;
import java.util.Date;
import static podcast.utils.Constants.*;

/**
 * An element from listening history of a user
 */
public class ListeningHistory extends Entity {

  @Getter private Type type = Type.listeningHistory;
  @Getter private String episodeId;
  @Getter private String seriesTitle;
  @Getter private String title;
  @Getter private String imageUrlSm;
  @Getter private String imageUrlLg;
  @Getter private String audioUrl;
  @Getter private String userId;
  @Getter private Date createdAt = new Date();

  /**
   * Constructor from episode and userId
   * @param episode - Episode
   * @param userId - String
   */
  public ListeningHistory(Episode episode, String userId) {
    this.episodeId = episode.getId();
    this.seriesTitle = episode.getSeriesTitle();
    this.title = episode.getTitle();
    this.imageUrlSm = episode.getImageUrlSm();
    this.imageUrlLg = episode.getImageUrlLg();
    this.audioUrl = episode.getAudioUrl();
    this.userId = userId;
  }

  /**
   * Constructor from JsonObject
   * @param object - JsonObject
   */
  public ListeningHistory(JsonObject object) {
    this.episodeId = object.getString(EPISODE_ID);
    this.seriesTitle = object.getString(SERIES_TITLE);
    this.title = object.getString(TITLE);
    this.imageUrlSm = object.getString(IMAGE_URL_SM);
    this.imageUrlLg = object.getString(IMAGE_URL_LG);
    this.audioUrl = object.getString(AUDIO_URL);
    this.userId = object.getString(USER_ID);
  }

  /** See {@link Entity#toJsonDocument()} **/
  public JsonDocument toJsonDocument() {
    return JsonDocument.create(composeKey(this), super.toJsonObject());
  }

  /** Compose a key from episodeId and userId **/
  public static String composeKey(String episodeId, String userId) {
    return Entity.composeKey(String.format("%s:%s", episodeId, userId), Type.listeningHistory.toString());
  }

  /** Compose a key from episode and user **/
  public static String composeKey(Episode episode, User user) {
    return composeKey(episode.getId(), user.getId());
  }

  /** Compose a key from ListeningHistory element **/
  public static String composeKey(ListeningHistory history) {
    return composeKey(history.getEpisodeId(), history.getUserId());
  }
}
