package integration;

import org.codehaus.jackson.JsonNode;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import podcast.utils.Constants;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/* Search tests */
public class SearchTest extends BaseIntegrationTest {

  @Before
  public void before() throws Exception {
    super.before();
    TimeUnit.SECONDS.sleep(1);
  }

  @Test
  public void testEpisodeSearch() throws Exception {
    // Parse response
    JsonNode response = mvcResultAsJson(
      getMockMvc()
      .perform(MockMvcRequestBuilders.get("/api/v1/search/episodes/Pl?offset=0&max=10")
        .header(Constants.AUTHORIZATION, Constants.BEARER + getSession()))
      .andExpect(MockMvcResultMatchers.status().isOk()).andReturn()
    );
    System.out.println(response);
  }

  @Test
  public void testSeriesSearch() throws Exception {
    // Parse response
    JsonNode response = mvcResultAsJson(
      getMockMvc()
      .perform(MockMvcRequestBuilders.get("/api/v1/search/series/Pl?offset=0&max=10")
        .header(Constants.AUTHORIZATION, Constants.BEARER + getSession()))
      .andExpect(MockMvcResultMatchers.status().isOk()).andReturn()
    );
    System.out.println(response);
  }

  @Test
  public void testUsersSearch() throws Exception {
    // Parse response
    JsonNode response = mvcResultAsJson(
      getMockMvc()
        .perform(MockMvcRequestBuilders.get("/api/v1/search/users/u?offset=0&max=10")
          .header(Constants.AUTHORIZATION, Constants.BEARER + getSession()))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn()
    );
    System.out.println(response);
  }


  @Test
  public void testAllSearch() throws Exception {
    // Parse response
    JsonNode response = mvcResultAsJson(
      getMockMvc()
        .perform(MockMvcRequestBuilders.get("/api/v1/search/all/Design?offset=0&max=10")
          .header(Constants.AUTHORIZATION, Constants.BEARER + getSession()))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn()
    );
    System.out.println(response);
  }


}
