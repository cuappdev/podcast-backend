package podcast.controllers;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import podcast.models.entities.podcasts.Episode;
import podcast.models.entities.podcasts.Series;
import podcast.models.entities.users.User;
import podcast.models.formats.Failure;
import podcast.models.formats.Result;
import podcast.models.formats.Success;
import podcast.services.PodcastsService;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static podcast.utils.Constants.*;

/**
 * Podcasts (series, episodes) REST API controller
 */
@RestController
@RequestMapping("/api/v1/podcasts")
public class PodcastsController {

  private final PodcastsService podcastsService;

  @Autowired
  public PodcastsController(PodcastsService podcastsService) {
    this.podcastsService = podcastsService;
  }

  /** Get a series by seriesId **/
  @RequestMapping(method = RequestMethod.GET, value = "/series/{series_id}")
  public ResponseEntity<Result> getSeriesById(@PathVariable("series_id") Long seriesId) {
    try {
      Series series = podcastsService.getSeries(seriesId);
      return ResponseEntity.status(200).body(new Success(SERIES, series));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(400).body(new Failure(e.getMessage()));
    }
  }

  /** Get episode by episodeId **/
  @RequestMapping(method = RequestMethod.GET, value="/episodes/{episode_id}")
  public ResponseEntity<Result> getEpisodeById(@PathVariable("episode_id") String episodeId) {
    try {
      Episode episode = podcastsService.getEpisode(episodeId);
      return ResponseEntity.status(200).body(new Success(EPISODE, episode));
    } catch (Exception e) {
      return ResponseEntity.status(400).body(new Failure(e.getMessage()));
    }
  }

  /** Get episodes by seriesId **/
  @RequestMapping(method = RequestMethod.GET, value = "/episodes/by_series/{series_id}")
  public ResponseEntity<Result> getEpisodesBySeriesId(@PathVariable("series_id") Long seriesId,
                                                      @RequestParam("offset") Integer offset,
                                                      @RequestParam("max") Integer max) {
    try {
      List<Episode> episodes = podcastsService.getEpisodesBySeriesId(seriesId, offset, max);
      return ResponseEntity.status(200).body(new Success(EPISODES, episodes));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(400).body(new Failure(e.getMessage()));
    }
  }

  /** Episodes info */
  @RequestMapping(method = RequestMethod.GET, value = "/episodes/info")
  public ResponseEntity<Result> episodesInfo(HttpServletRequest request,
                                             @RequestParam("ids") String episodeIds) {
    User user = (User) request.getAttribute(USER);
    try {
      List<String> idList = Arrays.asList(episodeIds.split(","));
      PodcastsService.EpisodesInfo episodesInfo = podcastsService.getEpisodesInfo(user.getId(), idList);
      return ResponseEntity.status(200).body(new Success(EPISODES_INFO, episodesInfo));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(400).body(new Failure(e.getMessage()));
    }
  }

  /** Series info */
  @RequestMapping(method = RequestMethod.GET, value = "/series/info")
  public ResponseEntity<Result> seriesInfo(HttpServletRequest request,
                                           @RequestParam("ids") String seriesIds) {
    User user = (User) request.getAttribute(USER);
    try {
      List<Long> idList = Arrays.asList(seriesIds.split(",")).stream().map(Long::parseLong).collect(Collectors.toList());
      PodcastsService.SeriesInfo seriesInfo = podcastsService.getSeriesInfo(user.getId(), idList);
      return ResponseEntity.status(200).body(new Success(SERIES_INFO, seriesInfo));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(400).body(new Failure(e.getMessage()));
    }
  }

}
