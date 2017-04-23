package podcast.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import podcast.models.entities.bookmarks.Bookmark;
import podcast.models.entities.users.User;
import podcast.models.formats.Failure;
import podcast.models.formats.Result;
import podcast.models.formats.Success;
import podcast.services.BookmarksService;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import static podcast.utils.Constants.*;

/**
 * Bookmarks REST API controller
 */
@RestController
@RequestMapping("/api/v1/bookmarks")
public class BookmarksController {

  private final BookmarksService bookmarksService;

  @Autowired
  public BookmarksController(BookmarksService bookmarksService) {
    this.bookmarksService = bookmarksService;
  }

  /** Create a bookmark */
  @RequestMapping(method = RequestMethod.POST, value = "/{episode_id}")
  public ResponseEntity<Result> createBookmark(HttpServletRequest request,
                                               @PathVariable("episode_id") String episodeId) {
    User user = (User) request.getAttribute(USER);
    try {
      Bookmark bookmark = bookmarksService.createBookmark(user, episodeId);
      return ResponseEntity.status(200).body(new Success(BOOKMARK, bookmark));
    } catch (Exception e) {
      return ResponseEntity.status(400).body(new Failure(e.getMessage()));
    }
  }

  /** Get a user's bookmarks */
  @RequestMapping(method = RequestMethod.GET, value = "/")
  public ResponseEntity<Result> getUserBookmarks(HttpServletRequest request) {
    User user = (User) request.getAttribute(USER);
    try {
      List<Bookmark> bookmarks = bookmarksService.getUserBookmarks(user);
      return ResponseEntity.status(200).body(new Success(BOOKMARKS, bookmarks));
    } catch (Exception e) {
      return ResponseEntity.status(400).body(new Failure(e.getMessage()));
    }
  }

  /** Delete a bookmark */
  @RequestMapping(method = RequestMethod.DELETE, value = "/{episode_id}")
  public ResponseEntity<Result> deleteBookmark(HttpServletRequest request,
                                               @PathVariable("episode_id") String episodeId) {
    User user = (User) request.getAttribute(USER);
    try {
      Bookmark bookmark = bookmarksService.deleteBookmark(user, episodeId);
      return ResponseEntity.status(200).body(new Success(BOOKMARK, bookmark));
    } catch (Exception e) {
      return ResponseEntity.status(400).body(new Failure(e.getMessage()));
    }
  }
}
