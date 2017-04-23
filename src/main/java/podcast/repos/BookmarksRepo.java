package podcast.repos;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import podcast.models.entities.bookmarks.Bookmark;
import podcast.models.entities.users.User;
import java.util.List;
import java.util.stream.Collectors;
import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.dsl.Expression.s;
import static com.couchbase.client.java.query.dsl.Expression.x;
import static podcast.utils.Constants.*;
import static podcast.utils.Constants.ID;

@Component
public class BookmarksRepo {

  private Bucket bucket;

  @Autowired
  public BookmarksRepo(@Qualifier("dbBucket") Bucket bucket) {
    this.bucket = bucket;
  }

  /** Stores a bookmark */
  public Bookmark storeBookmark(Bookmark bookmark) {
    bucket.upsert(bookmark.toJsonDocument());
    return bookmark;
  }

  /** Deletes a bookmark */
  public Bookmark deleteBookmark(Bookmark bookmark) {
    if (bookmark == null) return null;
    bucket.remove(Bookmark.composeKey(bookmark));
    return bookmark;
  }

  /** Get a bookmark, given user and episodeId */
  public Bookmark getBookmark(User user, String episodeId) {
    JsonDocument doc = bucket.get(Bookmark.composeKey(episodeId, user.getId()));
    if (doc == null) {
      return null;
    } else {
      return new Bookmark(doc.content());
    }
  }

  /** Get user's bookmarks */
  public List<Bookmark> getUserBookmarks(User user) {
    N1qlQuery q = N1qlQuery.simple(
      select("*").from("`" + DB + "`")
        .where(
          (x(TYPE).eq(s(BOOKMARK)))
            .and(x("`" + USER + "`.`" + ID + "`").eq(s(user.getId())))
        )
    );
    List<N1qlQueryRow> rows = bucket.query(q).allRows();
    return rows.stream()
      .map(r -> new Bookmark(r.value().getObject(DB))).collect(Collectors.toList());
  }

}
