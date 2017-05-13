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
  @Getter private Episode episode;
  @Getter private AssociatedUser user;

  /**
   * Constructor from user and episode
   * @param user - User
   * @param episode _ Episode
   */
  public Bookmark(User user, Episode episode) {
    this.episode = episode;
    this.user = new AssociatedUser(user);
  }

  /**
   * Constructor from JsonObject
   * @param object - JsonObject
   */
  public Bookmark(JsonObject object) {
    this.episode = new Episode(object.getObject(EPISODE));
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
    return composeKey(r.getEpisode().getId(), r.getUser().getId());
  }

}
