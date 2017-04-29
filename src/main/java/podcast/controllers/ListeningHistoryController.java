package podcast.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import podcast.models.entities.history.ListeningHistory;
import podcast.models.entities.users.User;
import podcast.models.formats.Failure;
import podcast.models.formats.Result;
import podcast.models.formats.Success;
import podcast.services.ListeningHistoryService;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import static podcast.utils.Constants.*;

@RestController
@RequestMapping("/api/v1/history/listening")
public class ListeningHistoryController {

  private final ListeningHistoryService listeningHistoryService;

  @Autowired
  public ListeningHistoryController(ListeningHistoryService listeningHistoryService) {
    this.listeningHistoryService = listeningHistoryService;
  }

  @RequestMapping(method = RequestMethod.GET, value = "")
  public ResponseEntity<Result> getUserListeningHistory(HttpServletRequest request,
                                                        @RequestParam("offset") Integer offset,
                                                        @RequestParam("max") Integer max) {
    User user = (User) request.getAttribute(USER);
    try {
      List<ListeningHistory> listeningHistories =
        listeningHistoryService.getUserListeningHistory(user.getId(), offset, max);
      return ResponseEntity.status(200).body(new Success(LISTENING_HISTORIES, listeningHistories));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(400).body(new Failure(e.getMessage()));
    }
  }

  /** Clear your listening history **/
  @RequestMapping(method = RequestMethod.DELETE, value = "")
  public ResponseEntity<Result> clearListeningHistory(HttpServletRequest request) {
    User user = (User) request.getAttribute(USER);
    try {
      listeningHistoryService.clearListeningHistory(user.getId());
      return ResponseEntity.status(200).body(new Success());
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(400).body(new Failure(e.getMessage()));
    }
  }

  /** Create a listening history element **/
  @RequestMapping(method = RequestMethod.POST, value = "/{episode_id}")
  public ResponseEntity<Result> createListeningHistory(HttpServletRequest request,
                                                       @PathVariable("episode_id") String episodeId) {
    User user = (User) request.getAttribute(USER);
    try {
      ListeningHistory listeningHistory = listeningHistoryService.createListeningHistory(user.getId(), episodeId);
      return ResponseEntity.status(200).body(new Success(LISTENING_HISTORY, listeningHistory));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(400).body(new Failure(e.getMessage()));
    }
  }

  @RequestMapping(method = RequestMethod.DELETE, value = "/{episode_id}")
  public ResponseEntity<Result> deleteListeningHistory(HttpServletRequest request,
                                                       @PathVariable("episode_id") String episodeId) {
    User user = (User) request.getAttribute(USER);
    try {
      ListeningHistory listeningHistory = listeningHistoryService.deleteListeningHistory(user.getId(), episodeId);
      return ResponseEntity.status(200).body(new Success(LISTENING_HISTORY, listeningHistory));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(400).body(new Failure(e.getMessage()));
    }
  }

}
