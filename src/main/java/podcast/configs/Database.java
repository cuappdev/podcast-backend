package podcast.configs;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
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
  public Database(@Value("${storage.cluster.host}") String clusterHost) {
    this.couchbaseCluster = CouchbaseCluster.create(clusterHost);
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
