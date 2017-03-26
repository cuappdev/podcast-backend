package podcast.configs;

import com.couchbase.client.core.retry.FailFastRetryStrategy;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
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

  @Value("${storage.dbBucket}")
  private String dbBucket;

  @Value("${storage.dbBucketPassword}")
  private String dbBucketPassword;

  @Value("${storage.podcastsBucket}")
  private String podcastsBucket;

  @Value("${storage.podcastsBucketPassword}")
  private String podcastsBucketPassword;

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
  public Bucket dbBucket() {
    if (dbBucketPassword == null || dbBucketPassword.length() == 0) {
      return couchbaseCluster.openBucket(dbBucket);
    } else {
      return couchbaseCluster.openBucket(dbBucket, dbBucketPassword);
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




}
