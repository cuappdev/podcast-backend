package podcast.models.entities.bookmarks;

import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import lombok.Getter;
import podcast.models.entities.Entity;
import podcast.models.entities.podcasts.Episode;
import podcast.models.entities.users.AssociatedUser;
import podcast.models.entities.users.User;
import podcast.utils.Constants;
import static podcast.utils.Constants.*;

/**
 * A user's bookmark of a particular podcast episode
 */
public class Bookmark extends Entity {

  // title, pubDate, duration, seriesTitle, id, numRecommendations, audioUrl
  @Getter private Constants.Type type = Constants.Type.bookmark;
  @Getter private String episodeId;
  @Getter private String title;
  @Getter private Long pubDate;
  @Getter private String duration;
  @Getter private String seriesTitle;
  @Getter private Integer numberRecommenders;
  @Getter private String audioUrl;
  @Getter private AssociatedUser user;

  /** Id getter */
  public String getId() {
    return composeKey(this);
  }

  /**
   * Constructor from user and episode
   * @param user - User
   * @param episode _ Episode
   */
  public Bookmark(User user, Episode episode) {
    this.episodeId = episode.getId();
    this.title = episode.getTitle();
    this.pubDate = episode.getPubDate();
    this.duration = episode.getDuration();
    this.seriesTitle = episode.getSeriesTitle();
    this.numberRecommenders = episode.getNumberRecommenders();
    this.audioUrl = episode.getAudioUrl();
    this.user = new AssociatedUser(user);
  }

  /**
   * Constructor from JsonObject
   * @param object - JsonObject
   */
  public Bookmark(JsonObject object) {
    this.episodeId = object.getString(EPISODE_ID);
    this.title = object.getString(TITLE);
    this.pubDate = object.getLong(PUB_DATE);
    this.duration = object.getString(DURATION);
    this.seriesTitle = object.getString(SERIES_TITLE);
    this.numberRecommenders = object.getInt(NUMBER_RECOMMENDERS);
    this.audioUrl = object.getString(AUDIO_URL);
    this.user = new AssociatedUser(object.getObject(USER));
  }

  /** See {@link Entity#toJsonDocument()} */
  public JsonDocument toJsonDocument() {
    return JsonDocument.create(composeKey(this), super.toJsonObject());
  }

  /** Compose a key from episodeId and userId **/
  public static String composeKey(String episodeId, String userId) {
    return Entity.composeKey(String.format("%s:%s", episodeId, userId), Constants.Type.bookmark.toString());
  }

  /** Compose a key from episode and user **/
  public static String composeKey(Episode episode, User user) {
    return composeKey(episode.getId(), user.getId());
  }

  /** Compose key from Bookmark **/
  public static String composeKey(Bookmark r) {
    return composeKey(r.getEpisodeId(), r.getUser().getId());
  }

}
