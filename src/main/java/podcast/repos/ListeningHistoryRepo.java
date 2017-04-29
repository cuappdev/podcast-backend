package podcast.repos;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.couchbase.client.java.query.dsl.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import podcast.models.entities.history.ListeningHistory;
import java.util.List;
import java.util.stream.Collectors;
import static podcast.utils.Constants.*;
import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.Delete.deleteFrom;
import static com.couchbase.client.java.query.dsl.Expression.*;

@Component
public class ListeningHistoryRepo {

  Bucket bucket;

  @Autowired
  public ListeningHistoryRepo(@Qualifier("dbBucket") Bucket bucket) {
    this.bucket = bucket;
  }

  /** Stores a listening history element **/
  public ListeningHistory storeListeningHistory(ListeningHistory listeningHistory) {
    bucket.upsert(listeningHistory.toJsonDocument());
    return listeningHistory;
  }

  /** Deletes a listening history element **/
  public ListeningHistory deleteListeningHistory(ListeningHistory listeningHistory) {
    if (listeningHistory == null) return null;
    bucket.remove(ListeningHistory.composeKey(listeningHistory));
    return listeningHistory;
  }

  /** Get listening history by userId and episodeId **/
  public ListeningHistory getListeningHistory(String userId, String episodeId) {
    return new ListeningHistory(bucket.get(ListeningHistory.composeKey(episodeId, userId)).content());
  }

  /** Clears the listening history of a user **/
  public void removeAllListeningHistories(String userId) {
    N1qlQuery q = N1qlQuery.simple(
      deleteFrom(DB)
        .where(
          (x(TYPE).eq(s(LISTENING_HISTORY)))
          .and(x(USER_ID).eq(s(userId)))
        )
    );
    bucket.query(q);
  }

  /** Get the listening histories of a user **/
  public List<ListeningHistory> getUserListeningHistories(String userId, Integer offset, Integer max) {
    N1qlQuery q = N1qlQuery.simple(
      select("*").from("`" + DB + "`")
      .where(
        (x(TYPE).eq(s(LISTENING_HISTORY)))
          .and(x(USER_ID).eq(s(userId)))
      ).orderBy(Sort.desc(CREATED_AT)).limit(max).offset(offset)
    );
    List<N1qlQueryRow> rows = bucket.query(q).allRows();
    return rows.stream()
      .map(r -> new ListeningHistory(r.value().getObject(DB))).collect(Collectors.toList());
  }

}
