package integration;


import org.codehaus.jackson.JsonNode;
import org.junit.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import podcast.utils.Constants;

public class PodcastsTest extends BaseIntegrationTest {

  @Test
  public void episodesBySeriesId() throws Exception {
    JsonNode response = mvcResultAsJson(
      getMockMvc()
        .perform(MockMvcRequestBuilders.get("/api/v1/podcasts/episodes/" + 1001228357 + "?offset=0&max=10")
        .header(Constants.AUTHORIZATION, Constants.BEARER + getSession()))
      .andExpect(MockMvcResultMatchers.status().isOk()).andReturn()
    );
  }
}
