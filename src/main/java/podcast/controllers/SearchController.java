package podcast.controllers;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import podcast.search.PodcastsSearch;
import podcast.search.UsersSearch;

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


  // TODO - endpoints

}
