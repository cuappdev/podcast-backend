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
import org.springframework.test.web.servlet.request.*;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import podcast.controllers.SessionsController;
import podcast.controllers.UsersController;

/* Session tests */
@SpringBootTest(classes = AppConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class SessionTests {

  /* Mock API configuration */
  @Autowired private SessionsController sessionsController;
  @Autowired private UsersController usersController;
  private ObjectMapper mapper = new ObjectMapper();
  private MockMvc mockMvc;

  @Before
  public void setup() {
    this.mockMvc =
      MockMvcBuilders.standaloneSetup(sessionsController, usersController).build();
  }

  @Test
  public void blank() {

  }

  public void test1() throws Exception {
    String idToken = System.getenv("TEST_ID_TOKEN");
    MvcResult result =
      this.mockMvc
      .perform(MockMvcRequestBuilders.post("/api/v1/users/google_sign_in").param("id_token", idToken))
      .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

    JsonNode response = mapper.readTree(result.getResponse().getContentAsString());
    String sessionToken = response.get("data").get("user").get("session").get("sessionToken").asText();
    System.out.println(sessionToken);
  }



}
