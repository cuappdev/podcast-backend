package podcast.configs;

import com.couchbase.client.java.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import podcast.authentication.UserAuthenticationInterceptor;

@Configuration
public class AppConfig extends WebMvcConfigurerAdapter {

  private Bucket userBucket;

  @Autowired
  public AppConfig(@Qualifier("usersBucket") Bucket userBucket) {
    this.userBucket = userBucket;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(
      new UserAuthenticationInterceptor(userBucket))
      .addPathPatterns("/api/v1/users/change_username")
      .addPathPatterns("/api/v1/podcasts/**")
      .addPathPatterns("/api/v1/followings/**")
      .addPathPatterns("/api/v1/search/**");

    // Add more patterns

    // Add more interceptors if necessary
  }
}