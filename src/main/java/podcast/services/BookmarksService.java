package podcast.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import podcast.models.entities.bookmarks.Bookmark;
import podcast.models.entities.podcasts.Episode;
import podcast.models.entities.users.User;
import podcast.repos.BookmarksRepo;
import podcast.repos.PodcastsRepo;
import podcast.repos.UsersRepo;
import java.util.List;

@Service
public class BookmarksService {

  private final PodcastsRepo podcastsRepo;
  private final UsersRepo usersRepo;
  private final BookmarksRepo bookmarksRepo;

  @Autowired
  public BookmarksService(PodcastsRepo podcastsRepo,
                          UsersRepo usersRepo,
                          BookmarksRepo bookmarksRepo) {
    this.podcastsRepo = podcastsRepo;
    this.usersRepo = usersRepo;
    this.bookmarksRepo = bookmarksRepo;
  }

  /** Create a bookmark */
  public Bookmark createBookmark(User owner, String episodeId) {
    Episode episode = podcastsRepo.getEpisodeById(episodeId);
    Bookmark bookmark = new Bookmark(owner, episode);
    return bookmarksRepo.storeBookmark(bookmark);
  }

  /** Delete a bookmark */
  public Bookmark deleteBookmark(User owner, String episodeId) {
    Bookmark bookmark = bookmarksRepo.getBookmark(owner, episodeId);
    return bookmarksRepo.deleteBookmark(bookmark);
  }

  /** Get bookmarks of a user */
  public List<Bookmark> getUserBookmarks(User user) {
    return bookmarksRepo.getUserBookmarks(user);
  }

 }
