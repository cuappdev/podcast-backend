package podcast.models.entities.stats;

import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import lombok.Getter;
import podcast.models.entities.Entity;
import static podcast.utils.Constants.*;

/**
 * Stats regarding a series
 */
public class SeriesStat extends Entity {

  @Getter private Type type = Type.seriesStats;
  @Getter private Long seriesId;
  @Getter private Integer numberSubscribers;

  /**
   * Constructor from seriesId
   * @param seriesId - String
   */
  public SeriesStat(Long seriesId) {
    this.seriesId = seriesId;
    this.numberSubscribers = 0;
  }

  /**
   * Constructor from JsonObject
   * @param object - JsonObject
   */
  public SeriesStat(JsonObject object) {
    this.seriesId = object.getLong(SERIES_ID);
    this.numberSubscribers = object.getInt(NUMBER_SUBSCRIBERS);
  }

  /** Increment the number of people who have subscribed to this.
   * Static b/c original JsonDocument returned in order to ensure CAS value is unchanged */
  public static JsonDocument incrementSubscriberCount(JsonDocument doc) {
    if (doc == null) return doc;
    assert doc.content().getString(TYPE).equals(SERIES_STAT);
    Integer originalNum = doc.content().getInt(NUMBER_SUBSCRIBERS) != null ?
      doc.content().getInt(NUMBER_SUBSCRIBERS) : 0;
    doc.content().put(NUMBER_SUBSCRIBERS, originalNum + 1);
    return doc;
  }

  /** Decrement the number of people who have subscribed to this.
   * Static b/c original JsonDocument returned in order to ensure CAS value is unchanged */
  public static JsonDocument decrementSubscriberCount(JsonDocument doc) {
    if (doc == null) return doc;
    assert doc.content().getString(TYPE).equals(SERIES_STAT);
    Integer originalNum = doc.content().getInt(NUMBER_SUBSCRIBERS) != null ?
      doc.content().getInt(NUMBER_SUBSCRIBERS) : 0;
    doc.content().put(NUMBER_SUBSCRIBERS, originalNum - 1);
    return doc;
  }

  /** See {@link Entity#toJsonDocument()} */
  public JsonDocument toJsonDocument() {
    return JsonDocument.create(composeKey(this), super.toJsonObject());
  }

  /** Compose key from seriesId */
  public static String composeKey(Long seriesId) {
    return Entity.composeKey("" + seriesId, Type.seriesStats.toString());
  }

  /** Compose key from seriesStats */
  public static String composeKey(SeriesStat seriesStats) {
    return composeKey(seriesStats.getSeriesId());
  }
}
