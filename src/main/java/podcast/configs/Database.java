package podcast.configs;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
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

  @Autowired
  private Cluster couchbaseCluster;

  @Bean
  public Bucket usersBucket() {
    return couchbaseCluster.openBucket(usersBucket, usersBucketPassword);
  }

  @Bean
  public Bucket podcastsBucket() {
    return couchbaseCluster.openBucket(podcastsBucket, podcastsBucketPassword);
  }

}
