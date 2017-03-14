package podcast.models.entities;

import com.couchbase.client.java.document.json.JsonObject;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import static podcast.utils.Constants.*;

/**
 * Podcast episode (e.g. an episode of 'Serial')
 */
public class Episode extends Podcast {

  @Getter private Type type = Type.EPISODE;
  @Getter private Long seriesId;
  @Getter private String seriesTitle;
  @Getter private String imageUrlSm;
  @Getter private String imageUrlLg;
  @Getter private String title;
  @Getter private String author;
  @Getter private String summary;
  @Getter private Date pubDate;
  @Getter private String duration;
  @Getter private String audioUrl;
  @Getter private List<String> tags;


  /**
   * Constructor from Couchbase JsonObject
   * @param object - JsonObject
   */
  public Episode(JsonObject object) {
    this.seriesId = object.getLong(SERIES_ID);
    this.seriesTitle = object.getString(SERIES_TITLE);
    this.imageUrlSm = object.getString(IMAGE_URL_SM);
    this.imageUrlLg = object.getString(IMAGE_URL_LG);
    this.title = object.getString(TITLE);
    this.author = object.getString(AUTHOR);
    this.summary = object.getString(SUMMARY);
    this.pubDate = object.getLong(PUB_DATE) == null ? null : new Date(object.getLong(PUB_DATE));
    this.duration = object.getString(DURATION);
    this.audioUrl = object.getString(AUDIO_URL);
    this.tags = object.getArray(TAGS) == null ? new ArrayList<String>() : object.getArray(TAGS).toList()
      .stream().map(o -> { return (String) o; }).collect(Collectors.toList());;
  }


}
