package podcast.configs;

import com.couchbase.client.core.env.QueryServiceConfig;
import com.couchbase.client.core.retry.FailFastRetryStrategy;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.cluster.ClusterManager;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Couchbase DB configuration
 */
@Configuration
public class Database {

  @Value("${storage.usersBucket}")
  private String usersBucket;

  @Value("${storage.usersBucketPassword}")
  private String usersBucketPassword;

  @Value("${storage.podcastsBucket}")
  private String podcastsBucket;

  @Value("${storage.podcastsBucketPassword}")
  private String podcastsBucketPassword;

  @Value("${storage.followersfollowingsBucket}")
  private String followersfollowingsBucket;

  @Value("${storage.followersfollowingsBucketPassword}")
  private String followersfollowingsBucketPassword;

  private Cluster couchbaseCluster;


  @Autowired
  public Database(@Value("${storage.cluster.host}") String clusterHost,
                  @Value("${storage.cluster.timeout}") Integer timeout) {

    CouchbaseEnvironment env = DefaultCouchbaseEnvironment.builder()
      .queryTimeout(timeout)
      .socketConnectTimeout(timeout)
      .connectTimeout(timeout)
      .kvTimeout(timeout)
      .computationPoolSize(5)
      .retryStrategy(FailFastRetryStrategy.INSTANCE)
      .build();

    this.couchbaseCluster = CouchbaseCluster.create(env, clusterHost);
  }


  @Bean
  public Bucket usersBucket() {
    if (usersBucketPassword == null || usersBucketPassword.length() == 0) {
      return couchbaseCluster.openBucket(usersBucket);
    } else {
      return couchbaseCluster.openBucket(usersBucket, usersBucketPassword);
    }
  }


  @Bean
  public Bucket podcastsBucket() {
    if (podcastsBucketPassword == null || podcastsBucketPassword.length() == 0) {
      return couchbaseCluster.openBucket(podcastsBucket);
    } else {
      return couchbaseCluster.openBucket(podcastsBucket, podcastsBucketPassword);
    }
  }


  @Bean
  public Bucket followersfollowingsBucket() {
    if (followersfollowingsBucketPassword == null || followersfollowingsBucketPassword.length() == 0) {
      return couchbaseCluster.openBucket(followersfollowingsBucket);
    } else {
      return couchbaseCluster.openBucket(followersfollowingsBucket, followersfollowingsBucketPassword);
    }
  }


}
