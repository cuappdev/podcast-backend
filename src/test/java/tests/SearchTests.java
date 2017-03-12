package tests;

import config.AppConfig;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import podcast.controllers.SearchController;
import podcast.controllers.UsersController;

/* Search tests */
@SpringBootTest(classes = AppConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class SearchTests {

  /* Mock API configuration */
  @Autowired private UsersController usersController;
  @Autowired private SearchController searchController;
  private ObjectMapper mapper = new ObjectMapper();
  private MockMvc mockMvc;


  @Before
  public void setup() {
    this.mockMvc =
      MockMvcBuilders.standaloneSetup(usersController, searchController).build();
  }

  @Test
  public void test1() throws Exception {
    String idToken = System.getenv("TEST_ID_TOKEN");
    MvcResult result =
      mockMvc
        .perform(MockMvcRequestBuilders.post("/api/v1/users/google_sign_in").param("id_token", idToken))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

    JsonNode response = mapper.readTree(result.getResponse().getContentAsString());
    String sessionToken = response.get("data").get("user").get("session").get("sessionToken").asText();


    MvcResult result1 =
      mockMvc
        .perform(MockMvcRequestBuilders
          .get("/api/v1/search/episodes/Plan")
          .header("Authorization", "Grant " + sessionToken))
      .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    JsonNode response1 = mapper.readTree(result1.getResponse().getContentAsString());
    System.out.println(response1);




  }


}
