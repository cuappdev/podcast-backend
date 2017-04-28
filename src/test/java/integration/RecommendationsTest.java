package integration;

import lombok.Getter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import podcast.services.RecommendationsService;

public class RecommendationsTest extends BaseIntegrationTest {

  @Autowired
  @Getter RecommendationsService recommendationsService;

  @Before
  public void before() throws Exception {
    super.before();
  }

  private void recommendAll() throws Exception {

  }

  @Test
  public void createRecommendationTest() throws Exception {
    // TODO
  }

  @Test
  public void deleteRecommendationTest() throws Exception {
    // TODO
  }

  @Test
  public void getUserRecommendationsTest() throws Exception {
    // TODO
  }

  @After
  public void cleanup() throws Exception {
    // TODO
    super.cleanup();
  }
}
