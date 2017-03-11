package tests;

import config.AppConfig;
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

// For reference
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/* Session tests */
@SpringBootTest(classes = AppConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class SessionTests {

  /* Mock API configuration */
  @Autowired private SessionsController sessionsController;
  @Autowired private UsersController usersController;
  private MockMvc mockMvc;

  @Before
  public void setup() {
    this.mockMvc =
      MockMvcBuilders.standaloneSetup(sessionsController, usersController).build();
  }

  @Test
  public void test1() throws Exception {
    String idToken = System.getenv("TEST_ID_TOKEN");
    MvcResult result =
      this.mockMvc
      .perform(MockMvcRequestBuilders.post("/api/v1/users/google_sign_in").param("id_token", idToken))
      .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    System.out.println(result.getResponse().getContentAsString());
  }



}
