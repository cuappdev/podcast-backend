package integration;

import lombok.Getter;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import podcast.models.entities.Follower;
import podcast.models.entities.User;
import podcast.services.FollowersFollowingsService;
import podcast.services.UsersService;
import utils.BaseTest;
import utils.MockGoogleCreds;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public abstract class BaseIntegrationTest extends BaseTest {

  /* Web application context */
  @Autowired
  private WebApplicationContext wac;

  /* Mock MVC */
  @Getter private MockMvc mockMvc;

  /* Services */
  @Autowired @Getter FollowersFollowingsService ffService;
  @Autowired @Getter UsersService usersService;

  /* Mock Data */
  @Getter private List<User> mockUsers;

  /** Session to use in every request **/
  @Getter private String session;

  /** Setup mock MVC connector + seed the DB **/
  @Before
  public void before() throws Exception {

    // Setup connection
    mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

    // Contains seeded users
    mockUsers = new ArrayList<User>();

    // Seed the DB with users
    for (int i = 0; i < 10; i++) {
      mockUsers.add(usersService.createUser(new MockGoogleCreds(i).toJsonNode()));
    }

    this.session = mockUsers.get(0).getSession().getSessionToken();

  }


  /** Clean up the DB **/
  @After
  public void cleanup() throws Exception {
    // Remove all seeded users
    for (User u : getMockUsers()) {
      usersService.removeUserById(u.getId());

      /* Remove followers/followings */

      Optional<List<Follower>> maybeFollowers = ffService.getUserFollowers(u.getId());
      List<Follower> followers = maybeFollowers.orElse(new ArrayList<Follower>());
      for(Follower f : followers) {
        ffService.deleteFollowing(u, f.getId());
      }
    }

  }

}
