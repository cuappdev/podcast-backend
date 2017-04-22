package integration;

import org.codehaus.jackson.JsonNode;
import org.junit.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import podcast.models.entities.users.User;
import podcast.utils.Constants;

public class UsersTest extends BaseIntegrationTest {

  @Test
  public void getUserById() throws Exception {
    // Check everyone
    for (User u : getMockUsers()) {
      JsonNode response = mvcResultAsJson(
        getMockMvc()
          .perform(MockMvcRequestBuilders.get("/api/v1/users/" + u.getId())
            .header(Constants.AUTHORIZATION, Constants.BEARER + getSession()))
          .andExpect(MockMvcResultMatchers.status().isOk()).andReturn()
      );
    }
  }
}
