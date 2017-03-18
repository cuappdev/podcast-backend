package integration;

import org.codehaus.jackson.JsonNode;
import org.junit.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import podcast.models.entities.User;
import podcast.utils.Constants;
import static org.junit.Assert.*;

/** Followers-followings tests **/
public class FollowersFollowingsTest extends BaseIntegrationTest {

  @Test
  public void createFollowing() throws Exception {
    // Follow everyone
    for (User u : getMockUsers()) {
      // If it's me, don't follow myself
      if (u.getSession().getSessionToken().equals(getSession())) {
        continue;
      }

      JsonNode respose = mvcResultAsJson(
        getMockMvc()
        .perform(MockMvcRequestBuilders.post("/api/v1/followings/new?id=" + u.getId())
          .header(Constants.AUTHORIZATION, Constants.BEARER + getSession()))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn()
      );
    }
  }

  @Test
  public void getFollowings() throws Exception {

  }

  @Test
  public void getFollowers() throws Exception {

  }

  @Test
  public void deleteFollowings() throws Exception {

  }


}
