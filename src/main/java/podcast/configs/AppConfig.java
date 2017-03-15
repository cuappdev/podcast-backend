package podcast.configs;

import com.couchbase.client.java.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import podcast.authentication.UserAuthenticationInterceptor;

@Configuration
@EnableWebMvc
@ComponentScan("podcast")
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
      .addPathPatterns("/**")
      .excludePathPatterns("/api/v1/users/google_sign_in")
      .excludePathPatterns("/api/v1/sessions/update");

    // Add more interceptors if necessary
  }
}