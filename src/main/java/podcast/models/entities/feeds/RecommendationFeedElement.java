package podcast.models.entities.feeds;

import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import lombok.Getter;
import podcast.models.entities.Entity;
import podcast.models.entities.podcasts.Episode;
import podcast.models.entities.users.AssociatedUser;
import podcast.models.entities.users.User;
import podcast.utils.Constants;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import static podcast.utils.Constants.*;

/**
 * Recommended podcast feed element
 */
public class RecommendationFeedElement extends FeedElement {

  @Getter private FeedType feedType = FeedType.recommendationFeedElement;
  @Getter private String ownerId;
  @Getter private Episode episode;
  @Getter private List<AssociatedUser> users;
  @Getter private Date updatedAt = new Date();

  /**
   * Constructor from Episode and initial recommending user
   * @param ownerId - String (Id of the individual whose feed this will appear on)
   * @param episode - Episode
   * @param user - User
   */
  public RecommendationFeedElement(String ownerId, Episode episode, User user) {
    this.ownerId = ownerId;
    this.episode = episode;
    this.users = new ArrayList<AssociatedUser>();
    this.users.add(new AssociatedUser((user)));
  }

  /** Static update, including a user who recommended a specific episode **/
  public static JsonDocument addUser(JsonDocument doc, User user) {
    assert doc.content().getString(TYPE).equals(RECOMMENDATION_FEED_ELEMENT);
    AssociatedUser associatedUser = new AssociatedUser(user);
    JsonArray arr = doc.content().getArray(USERS);
    List<AssociatedUser> users = arr.toList().stream()
      .map(o -> {
        return new AssociatedUser((JsonObject) o);
      }).collect(Collectors.toList());
    HashSet<AssociatedUser> usersSet = new HashSet<AssociatedUser>(users);
    if (!usersSet.contains(associatedUser)) {
      arr.add(associatedUser);
      doc.content().put(UPDATED_AT, new Date());
    }
    doc.content().put(USERS, arr);
    return doc;
  }

  /** Add user to to this recommendation feed element **/
  public void addUser(User user) {
    this.users.add(new AssociatedUser(user));
  }

  /** See {@link Entity#toJsonDocument()} */
  public JsonDocument toJsonDocument() {
    return JsonDocument.create(composeKey(this), super.toJsonObject());
  }

  /** Compose key from RecommendationFeedElement **/
  public static String composeKey(RecommendationFeedElement feedElement) {
    return composeKey(feedElement.getOwnerId(), feedElement.getEpisode());
  }

  /** Compose key from ownerId and episode **/
  public static String composeKey(String ownerId, Episode episode) {
    return composeKey(ownerId, episode.getId());
  }

  /** Compose key from ownerId and episodeId **/
  public static String composeKey(String ownerId, String episodeId) {
    return Entity.composeKey(String.format("%s:%s", ownerId, episodeId), Constants.Type.feed.toString());
  }

}
