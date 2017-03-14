package integration;

import lombok.Getter;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import podcast.controllers.*;
import podcast.models.entities.User;
import podcast.services.FollowersFollowingsService;
import podcast.services.UsersService;
import utils.BaseTest;
import utils.MockGoogleCreds;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseIntegrationTest extends BaseTest {

  /* Mock API configuration */
  @Autowired private FollowersController followersController;
  @Autowired private FollowingsController followingsController;
  @Autowired private PodcastsController podcastsController;
  @Autowired private SearchController searchController;
  @Autowired private SessionsController sessionsController;
  @Autowired private UsersController usersController;
  @Getter private MockMvc mockMvc;

  /* Services */
  @Autowired @Getter FollowersFollowingsService followersFollowingsService;
  @Autowired @Getter UsersService usersService;

  /* Mock Data */
  @Getter private List<User> mockUsers;

  /** Session to use in every request **/
  @Getter private String session;

  /** Setup mock MVC connector + seed the DB **/
  @Before
  public void before() throws Exception {

    // Setup connection
    mockMvc =
      MockMvcBuilders.standaloneSetup(
        followersController,
        followingsController,
        podcastsController,
        searchController,
        sessionsController,
        usersController
      ).build();

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
    }

    // TODO - remove followers / followings

  }

}
