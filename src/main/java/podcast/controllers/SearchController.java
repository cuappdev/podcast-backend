package podcast.controllers;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import podcast.models.entities.Episode;
import podcast.models.entities.Person;
import podcast.models.entities.Series;
import podcast.models.entities.User;
import podcast.models.formats.Failure;
import podcast.models.formats.Result;
import podcast.models.formats.Success;
import podcast.search.PodcastsSearch;
import podcast.search.UsersSearch;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import java.util.List;

/**
 * Search REST API controller
 */
@RestController
@RequestMapping("/api/v1/search")
public class SearchController {

  private final PodcastsSearch podcastsSearch;
  private final UsersSearch usersSearch;

  private static final String QUERY = "query";
  private static final String OFFSET = "offset";
  private static final String MAX = "max";


  public SearchController(@Qualifier("simplePodcastsSearch") PodcastsSearch podcastsSearch,
                          @Qualifier("simpleUsersSearch") UsersSearch usersSearch) {
    this.podcastsSearch = podcastsSearch;
    this.usersSearch = usersSearch;
  }


  /** Search episodes **/
  @RequestMapping(method = RequestMethod.GET, value = "/episodes/{query}")
  public ResponseEntity<Result> searchEpisodes(HttpServletRequest request,
                                               @PathVariable(QUERY) String query,
                                               @RequestParam(OFFSET) Integer offset,
                                               @RequestParam(MAX) Integer max) {
    try {
      List<Episode> episodes = podcastsSearch.searchEpisodes(query, offset, max);
      return ResponseEntity.status(200).body(new Success("episodes", episodes));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(400).body(new Failure(e.getMessage()));
    }
  }


  /** Search series **/
  @RequestMapping(method = RequestMethod.GET, value = "/series/{query}")
  public ResponseEntity<Result> searchSeries(HttpServletRequest request,
                                             @PathVariable(QUERY) String query,
                                             @RequestParam(OFFSET) Integer offset,
                                             @RequestParam(MAX) Integer max) {
    try {
      List<Series> series = podcastsSearch.searchSeries(query, offset, max);
      return ResponseEntity.status(200).body(new Success("series", series));
    } catch (Exception e) {
      return ResponseEntity.status(400).body(new Failure(e.getMessage()));
    }
  }


  /** Search users **/
  @RequestMapping(method = RequestMethod.GET, value = "/users/{query}")
  public ResponseEntity<Result> searchUsers(HttpServletRequest request,
                                            @PathVariable(QUERY) String query,
                                            @RequestParam(OFFSET) Integer offset,
                                            @RequestParam(MAX) Integer max) {
    try {
      List<Person> users = usersSearch.searchUsers(query, offset, max);
      return ResponseEntity.status(200)
        .body(new Success("users", users));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(400).body(new Failure(e.getMessage()));
    }
  }


  /** Search everything **/
  @RequestMapping(method = RequestMethod.GET, value = "/all/{query}")
  public ResponseEntity<Result> searchAll(HttpServletRequest request,
                                          @PathVariable(QUERY) String query,
                                          @RequestParam(OFFSET) Integer offset,
                                          @RequestParam(MAX) Integer max) {
    try {
      List<Series> series = podcastsSearch.searchSeries(query, offset, max);
      List<Episode> episodes = podcastsSearch.searchEpisodes(query, offset, max);
      List<Person> users = usersSearch.searchUsers(query, offset, max);
      return ResponseEntity.status(200).body(
        new Success("series", series)
          .addField("episodes", episodes)
          .addField("users", users)
      );
    } catch (Exception e) {
      return ResponseEntity.status(400).body(new Failure(e.getMessage()));
    }
  }


}
