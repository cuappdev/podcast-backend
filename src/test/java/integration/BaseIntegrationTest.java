package integration;

import lombok.Getter;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import podcast.models.entities.podcasts.Episode;
import podcast.models.entities.podcasts.Series;
import podcast.models.entities.users.User;
import podcast.services.UsersService;
import podcast.services.PodcastsService;
import utils.BaseTest;
import utils.MockGoogleCreds;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class BaseIntegrationTest extends BaseTest {

  // Context for requesting
  @Autowired
  private WebApplicationContext wac;

  @Getter private MockMvc mockMvc;
  @Autowired @Getter UsersService usersService;
  @Autowired @Getter PodcastsService podcastsService;
  @Getter private List<User> mockUsers;
  @Getter private List<Series> mockSeries;
  @Getter private List<Episode> mockEpisode;
  @Getter private String session;

  /** Setup mock MVC connector + seed the DB */
  @Before
  public void before() throws Exception {

    // Setup connection
    mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

    // Contain seeded users and series
    this.mockUsers = new ArrayList<User>();
    this.mockSeries = new ArrayList<Series>();
    this.mockEpisode = new ArrayList<Episode>();

    // Seed the DB with users
    for (int i = 0; i < 10; i++) {
      MockGoogleCreds creds = new MockGoogleCreds(i);
      mockUsers.add(usersService.getOrCreateUser(creds.toJsonNode(), creds.getSub()).getValue());
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
    TimeUnit.SECONDS.sleep(1);
  }
}
