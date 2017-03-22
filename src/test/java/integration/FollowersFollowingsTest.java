package integration;

import lombok.Cleanup;
import lombok.Getter;
import org.codehaus.jackson.JsonNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import podcast.models.entities.Follower;
import podcast.models.entities.User;
import podcast.services.FollowersFollowingsService;
import podcast.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/** Followers-followings tests **/
public class FollowersFollowingsTest extends BaseIntegrationTest {

  @Autowired
  @Getter
  FollowersFollowingsService ffService;

  @Before
  public void before() throws Exception {
    super.before();
    for (User u : getMockUsers()) {
      // If it's me, don't follow myself
      if (u.getSession().getSessionToken().equals(getSession())) {
        continue;
      }
      ffService.createFollowing(getMockUsers().get(0), u.getId());
    }
  }

  /**
   * Returns a user guaranteed not to be the current user
   * @return
   */
  private String notMeId() {
    int NOT_ME = 2; // Need some user who's not "me"; arbitrarily picked 2
    return getMockUsers().get(NOT_ME).getId();
  }

  /**
   * Creates a following
   * @throws Exception
   */
  // How do we ignore the @Before for this test?
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
        .perform(MockMvcRequestBuilders.post("/api/v1/followings/?id=" + u.getId())
          .header(Constants.AUTHORIZATION, Constants.BEARER + getSession()))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn()
      );
    }
  }

  /**
   * Gets followings when the id is 'me'
   * @throws Exception
   */
  @Test
  public void getMyFollowings() throws Exception {
    JsonNode respose = mvcResultAsJson(
        getMockMvc()
            .perform(MockMvcRequestBuilders.get("/api/v1/followings/show?id=me")
                .header(Constants.AUTHORIZATION, Constants.BEARER + getSession()))
            .andExpect(MockMvcResultMatchers.status().isOk()).andReturn()
    );
  }

  /**
   * Gets followings for a user who's not 'me'
   * @throws Exception
   */
  @Test
  public void getFollowings() throws Exception {
    JsonNode respose = mvcResultAsJson(
        getMockMvc()
            .perform(MockMvcRequestBuilders.get("/api/v1/followings/show?id="+notMeId())
                .header(Constants.AUTHORIZATION, Constants.BEARER + getSession()))
            .andExpect(MockMvcResultMatchers.status().isOk()).andReturn()
    );
  }

  /**
   * Gets followers when the id is 'me'
   * @throws Exception
   */
  @Test
  public void getMyFollowers() throws Exception {
    JsonNode respose = mvcResultAsJson(
        getMockMvc()
            .perform(MockMvcRequestBuilders.get("/api/v1/followings/show?id=me")
                .header(Constants.AUTHORIZATION, Constants.BEARER + getSession()))
            .andExpect(MockMvcResultMatchers.status().isOk()).andReturn()
    );
  }

  /**
   * Gets followers for a user who's not 'me'
   * @throws Exception
   */
  @Test
  public void getFollowers() throws Exception {
    JsonNode respose = mvcResultAsJson(
        getMockMvc()
            .perform(MockMvcRequestBuilders.get("/api/v1/followings/show?id="+notMeId())
                .header(Constants.AUTHORIZATION, Constants.BEARER + getSession()))
            .andExpect(MockMvcResultMatchers.status().isOk()).andReturn()
    );
  }

  /**
   * Deletes followings
   * @throws Exception
   */
  @Test
  public void deleteFollowings() throws Exception {
    for (User u : getMockUsers()) {
      // If it's me, don't follow myself
      if (u.getSession().getSessionToken().equals(getSession())) {
        continue;
      }

      JsonNode respose = mvcResultAsJson(
          getMockMvc()
              .perform(MockMvcRequestBuilders.delete("/api/v1/followings/?id=" + u.getId())
                  .header(Constants.AUTHORIZATION, Constants.BEARER + getSession()))
              .andExpect(MockMvcResultMatchers.status().isOk()).andReturn()
      );
    }
  }

  @After
  public void cleanup() throws Exception {
    super.cleanup();
    for (User u : getMockUsers()) {
        /* Remove followers/followings */

      Optional<List<Follower>> maybeFollowers = ffService.getUserFollowers(u.getId());
      List<Follower> followers = maybeFollowers.orElse(new ArrayList<Follower>());
      for (Follower f : followers) {
        ffService.deleteFollowing(u, f.getId());
      }
    }
  }


}
