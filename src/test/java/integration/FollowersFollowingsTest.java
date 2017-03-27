package integration;

import lombok.Getter;
import org.codehaus.jackson.JsonNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import podcast.models.entities.User;
import podcast.services.FollowersFollowingsService;
import podcast.utils.Constants;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/** Followers-followings tests **/
public class FollowersFollowingsTest extends BaseIntegrationTest {

  @Autowired
  @Getter
  FollowersFollowingsService ffService;

  @Before
  public void before() throws Exception {
    super.before();
    followEveryone();
    TimeUnit.SECONDS.sleep(2);
  }

  private String notMeId() {
    int NOT_ME = 2; // Need some user who's not "me"; arbitrarily picked 2
    return getMockUsers().get(NOT_ME).getId();
  }

  private void followEveryone() throws Exception {
    // Follow everyone
    for (User u : getMockUsers()) {
      // If it's me, don't follow myself
      if (u.getSession().getSessionToken().equals(getSession())) {
        continue;
      }

      JsonNode response = mvcResultAsJson(
        getMockMvc()
          .perform(MockMvcRequestBuilders.post("/api/v1/followings/" + u.getId())
            .header(Constants.AUTHORIZATION, Constants.BEARER + getSession()))
          .andExpect(MockMvcResultMatchers.status().isOk()).andReturn()
      );
    }
  }

  @Test
  public void createFollowing() throws Exception {
    followEveryone();
  }

  @Test
  public void getMyFollowings() throws Exception {
    JsonNode response = mvcResultAsJson(
      getMockMvc()
        .perform(MockMvcRequestBuilders.get("/api/v1/followings/show?id=me")
          .header(Constants.AUTHORIZATION, Constants.BEARER + getSession()))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn()
    );
  }


  @Test
  public void getFollowings() throws Exception {
    JsonNode response = mvcResultAsJson(
      getMockMvc()
        .perform(MockMvcRequestBuilders.get("/api/v1/followings/show?id=" + notMeId())
          .header(Constants.AUTHORIZATION, Constants.BEARER + getSession()))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn()
    );
  }

  @Test
  public void getMyFollowers() throws Exception {
    // TODO - have people follow you
    JsonNode response = mvcResultAsJson(
      getMockMvc()
        .perform(MockMvcRequestBuilders.get("/api/v1/followings/show?id=me")
          .header(Constants.AUTHORIZATION, Constants.BEARER + getSession()))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn()
    );
  }

  @Test
  public void getFollowers() throws Exception {
    JsonNode response = mvcResultAsJson(
      getMockMvc()
        .perform(MockMvcRequestBuilders.get("/api/v1/followings/show?id=" + notMeId())
          .header(Constants.AUTHORIZATION, Constants.BEARER + getSession()))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn()
    );
  }


  @Test
  public void deleteFollowings() throws Exception {
    for (User u : getMockUsers()) {
      // If it's me, don't follow myself
      if (u.getSession().getSessionToken().equals(getSession())) {
        continue;
      }

      JsonNode response = mvcResultAsJson(
        getMockMvc()
          .perform(MockMvcRequestBuilders.delete("/api/v1/followings/" + u.getId())
            .header(Constants.AUTHORIZATION, Constants.BEARER + getSession()))
          .andExpect(MockMvcResultMatchers.status().isOk()).andReturn()
      );
    }
  }

  @After
  public void cleanup() throws Exception {

    // Clean up followings (try-catch b/c they might not exist)
    for (User u : getMockUsers()) {
      for (User u1 : getMockUsers()) {
        try {
          ffService.deleteFollowing(u, u1.getId());
        } catch (Exception e) {
        }
      }
    }
    // Then delete all users and such
    super.cleanup();
  }


}
