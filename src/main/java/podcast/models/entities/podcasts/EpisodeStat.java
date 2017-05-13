package podcast.models.entities.podcasts;

import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import lombok.Getter;
import podcast.models.entities.Entity;
import static podcast.utils.Constants.*;

/**
 * Stats regarding an episode
 */
public class EpisodeStat extends Entity {

  @Getter private Type type = Type.episodeStats;
  @Getter private String episodeId;
  @Getter private Integer numberRecommenders;

  /**
   * Constructor from episodeId
   * @param episodeId - String
   */
  public EpisodeStat(String episodeId) {
    this.episodeId = episodeId;
    this.numberRecommenders = 0;
  }

  /**
   * Constructor from JsonObject
   */
  public EpisodeStat(JsonObject object) {
    this.episodeId = object.getString(EPISODE_ID);
    this.numberRecommenders = object.getInt(NUMBER_RECOMMENDERS);
  }

  /** See {@link Entity#toJsonDocument()} */
  public JsonDocument toJsonDocument() {
    return JsonDocument.create(composeKey(this), super.toJsonObject());
  }

  /** Increment the number of people who have recommended this.
   * Static b/c original JsonDocument returned in order to ensure CAS value is unchanged */
  public static JsonDocument incrementNumberRecommenders(JsonDocument doc) {
    if (doc == null) return doc;
    assert doc.content().getString(TYPE).equals(EPISODE_STAT);
    Integer originalNum = doc.content().getInt(NUMBER_RECOMMENDERS) != null ?
      doc.content().getInt(NUMBER_RECOMMENDERS) : 0;
    doc.content().put(NUMBER_RECOMMENDERS, originalNum + 1);
    return doc;
  }

  /** Decrement the number of people who have recommended this.
   * Static b/c original JsonDocument returns in order to ensure CAS value is unchanged */
  public static JsonDocument decrementNumberRecommenders(JsonDocument doc) {
    if (doc == null) return doc;
    assert doc.content().getString(TYPE).equals(EPISODE_STAT);
    Integer originalNum = doc.content().getInt(NUMBER_RECOMMENDERS) != null ?
      doc.content().getInt(NUMBER_RECOMMENDERS) : 0;
    doc.content().put(NUMBER_RECOMMENDERS, originalNum - 1);
    return doc;
  }

  /** Compose Id from episodeId */
  public static String composeKey(String episodeId) {
    return Entity.composeKey(episodeId, Type.episodeStats.toString());
  }

  /** Compose Id from episodeStats */
  public static String composeKey(EpisodeStat episodeStats) {
    return composeKey(episodeStats.getEpisodeId());
  }
}
