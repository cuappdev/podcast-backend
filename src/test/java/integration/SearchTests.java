package integration;

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
public class SearchTests extends BaseIntegrationTest {


  @Test
  public void blank() {

  }


  public void test1() throws Exception {
    String idToken = System.getenv("TEST_ID_TOKEN");
    MvcResult result =
      getMockMvc()
        .perform(MockMvcRequestBuilders.post("/api/v1/users/google_sign_in").param("id_token", idToken))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

    JsonNode response = getMapper().readTree(result.getResponse().getContentAsString());
    String sessionToken = response.get("data").get("user").get("session").get("sessionToken").asText();

  }


}
