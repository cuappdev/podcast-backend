package podcast.controllers;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import podcast.models.entities.Episode;
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
  @RequestMapping(method = RequestMethod.GET, value="/episodes/{query}")
  public ResponseEntity<Result> searchEpisodes(HttpServletRequest request,
                                               @PathVariable("query") String query) {
    try {
      List<Episode> results = podcastsSearch.searchEpisodes(query);
      return ResponseEntity.status(200).body(new Success("episodes", results));
    } catch (Exception e) {
      return ResponseEntity.status(400).body(new Failure(e.getMessage()));
    }
  }




}
