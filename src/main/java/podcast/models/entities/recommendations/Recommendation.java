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
  @Getter private Episode episode;
  @Getter private AssociatedUser user;
  @Getter private Date createdAt = new Date();


  /**
   * Constructor from user and episode
   * @param user - User
   * @param episode _ Episode
   */
  public Recommendation(User user, Episode episode) {
    this.episode = episode;
    this.user = new AssociatedUser(user);
  }

  /**
   * Constructor from JsonObject
   * @param object - JsonObject
   */
  public Recommendation(JsonObject object) {
    this.episode = new Episode(object.getObject(EPISODE));
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
    return composeKey(r.getEpisode().getId(), r.getUser().getId());
  }

}
