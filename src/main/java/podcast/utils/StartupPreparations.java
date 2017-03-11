package podcast.utils;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.couchbase.client.java.query.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static com.couchbase.client.java.query.Index.PRIMARY_NAME;
import static com.couchbase.client.java.query.Index.createIndex;
import static com.couchbase.client.java.query.Index.createPrimaryIndex;
import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.dsl.Expression.i;
import static com.couchbase.client.java.query.dsl.Expression.s;
import static com.couchbase.client.java.query.dsl.Expression.x;

@Component
public class StartupPreparations implements InitializingBean {

  private static final Logger LOGGER = LoggerFactory.getLogger(StartupPreparations.class);

  /* Buckets */
  private final Bucket usersBucket;
  private final Bucket podcastsBucket;
  private final Bucket followersfollowingsBucket;


  /** Constructor **/
  public StartupPreparations(@Qualifier("usersBucket") Bucket usersBucket,
                             @Qualifier("podcastsBucket") Bucket podcastsBucket,
                             @Qualifier("followersfollowingsBucket") Bucket followersfollowingsBucket) {
    this.usersBucket = usersBucket;
    this.podcastsBucket = podcastsBucket;
    this.followersfollowingsBucket = followersfollowingsBucket;
  }


  @Override
  public void afterPropertiesSet() throws Exception {
    ensureIndexes();
  }


  /** Overall index function **/
  private void ensureIndexes() throws Exception {
    // User indexes
    ArrayList<String> usersIndexes =
      new ArrayList<String>(Arrays.asList("def_googleId", "def_firstName", "def_lastName"));
    ensureBucketIndexes(usersBucket, usersIndexes);

    // Podcast indexes
    ArrayList<String> podcastIndexes =
      new ArrayList<String>(Arrays.asList("def_title", "def_seriesTitle", "def_type"));
    ensureBucketIndexes(podcastsBucket, podcastIndexes);

    ArrayList<String> followersfollowingsIndexes =
        new ArrayList<String>(Arrays.asList("def_ownerId", "def_type"));
    ensureBucketIndexes(followersfollowingsBucket, followersfollowingsIndexes);

    // -- More buckets
  }


  /** Citing: https://goo.gl/9xRux5
   * HELPER THAT ALLOWS FOR THE SPECIFICATION OF INDEXES AT RUNTIME **/
  private void ensureBucketIndexes(Bucket bucket,
                                   ArrayList<String> indexesToCreate) throws Exception {

    LOGGER.info("Ensuring all Indexes are created.");

    N1qlQueryResult indexResult = bucket.query(
      N1qlQuery.simple(select("indexes.*")
        .from("system:indexes")
        .where(i("keyspace_id").eq(s(bucket.name()))))
    );


    boolean hasPrimary = false;
    List<String> foundIndexes = new ArrayList<String>();
    for (N1qlQueryRow indexRow : indexResult) {
      String name = indexRow.value().getString("name");
      Boolean isPrimary = indexRow.value().getBoolean("is_primary");
      if (name.equals(PRIMARY_NAME) || isPrimary == Boolean.TRUE) {
        hasPrimary = true;
      } else {
        foundIndexes.add(name);
      }
    }
    indexesToCreate.removeAll(foundIndexes);

    if (!hasPrimary) {
      // will create the primary index with default name "#primary".
      // Note that some tools may also create it under
      // the name "def_primary" (in which case hasPrimary should be true).
      Statement query = createPrimaryIndex().on(bucket.name()).withDefer();
      LOGGER.info("Executing index query: {}", query);
      N1qlQueryResult result = bucket.query(N1qlQuery.simple(query));
      if (result.finalSuccess()) {
        LOGGER.info("Successfully created primary index.");
      } else {
        LOGGER.warn("Could not create primary index: {}", result.errors());
      }
    }

    for (String name : indexesToCreate) {
      Statement query = createIndex(name).on(bucket.name(), x(name.replace("def_", ""))).withDefer();
      LOGGER.info("Executing index query: {}", query);
      N1qlQueryResult result = bucket.query(N1qlQuery.simple(query));
      if (result.finalSuccess()) {
        LOGGER.info("Successfully created index with name {}.", name);
      } else {
        LOGGER.warn("Could not create index {}: {}", name, result.errors());
      }
    }

    // prepare the list of indexes to build (both primary and secondary indexes)
    List<String> indexesToBuild = new ArrayList<String>(indexesToCreate.size()+1);
    indexesToBuild.addAll(indexesToCreate);
    if (!hasPrimary) {
      indexesToBuild.add(PRIMARY_NAME);
    }

    //skip the build step if all indexes have been found
    if (indexesToBuild.isEmpty()) {
      LOGGER.info("All indexes are already in place, nothing to build");
      return;
    }

    LOGGER.info("Waiting 5 seconds before building the indexes.");
    Thread.sleep(5000);

    // trigger the build
    StringBuilder indexes = new StringBuilder();
    for (int i = 0; i < indexesToBuild.size(); i++) {
      String nameWithTicks = "`" + indexesToBuild.get(i) + "`";
      nameWithTicks += i < indexesToBuild.size()-1 ? "," : "";
      indexes.append(nameWithTicks);
    }

    String query = "BUILD INDEX ON `" + bucket.name() + "` (" + indexes.toString() + ")";
    LOGGER.info("Executing index query: {}", query);
    N1qlQueryResult result = bucket.query(N1qlQuery.simple(query));
    if (result.finalSuccess()) {
      LOGGER.info("Successfully executed build index query.");
    } else {
      LOGGER.warn("Could not execute build index query {}.", result.errors());
    }
  }

}
