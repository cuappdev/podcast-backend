package podcast.controllers;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import podcast.models.entities.Episode;
import podcast.models.entities.Series;
import podcast.models.entities.User;
import podcast.models.formats.Failure;
import podcast.models.formats.Result;
import podcast.models.formats.Success;
import podcast.search.PodcastsSearch;
import podcast.search.UsersSearch;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Search REST API controller
 */
@RestController
@RequestMapping("/api/v1/search")
public class SearchController {

  private final PodcastsSearch podcastsSearch;
  private final UsersSearch usersSearch;

  public SearchController(@Qualifier("simplePodcastsSearch") PodcastsSearch podcastsSearch,
                          @Qualifier("simpleUsersSearch") UsersSearch usersSearch) {
    this.podcastsSearch = podcastsSearch;
    this.usersSearch = usersSearch;
  }


  /** Search episodes **/
  @RequestMapping(method = RequestMethod.GET, value = "/episodes/{query}")
  public ResponseEntity<Result> searchEpisodes(HttpServletRequest request,
                                               @PathVariable("query") String query) {
    try {
      List<Episode> results = podcastsSearch.searchEpisodes(query, 0, 0);
      return ResponseEntity.status(200).body(new Success("episodes", results));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(400).body(new Failure(e.getMessage()));
    }
  }


  /** Search series **/
  @RequestMapping(method = RequestMethod.GET, value = "/series/{query}")
  public ResponseEntity<Result> searchSeries(HttpServletRequest request,
                                             @PathVariable("query") String query) {
    try {
      List<Series> series = podcastsSearch.searchSeries(query, 0, 0);
      return ResponseEntity.status(200).body(new Success("series", series));
    } catch (Exception e) {
      return ResponseEntity.status(400).body(new Failure(e.getMessage()));
    }
  }


  /** Search everything **/
  @RequestMapping(method = RequestMethod.GET, value = "/all/{query}")
  public ResponseEntity<Result> searchAll(HttpServletRequest request,
                                          @PathVariable("query") String query) {
    try {
      List<Series> series = podcastsSearch.searchSeries(query, 0, 0);
      List<Episode> episodes = podcastsSearch.searchEpisodes(query, 0, 0);
      List<User> users = usersSearch.searchUsers(query, 0, 0);
      return ResponseEntity.status(200).body(
        new Success("series", series).addField("episodes", episodes).addField("users", users)
      );
    } catch (Exception e) {
      return ResponseEntity.status(400).body(new Failure(e.getMessage()));
    }
  }


}
