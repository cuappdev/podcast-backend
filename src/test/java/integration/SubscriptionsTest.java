package integration;

import org.codehaus.jackson.JsonNode;
import lombok.Getter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import podcast.models.entities.Series;
import podcast.models.entities.User;
import podcast.services.SubscriptionsService;
import podcast.utils.Constants;

import java.util.concurrent.TimeUnit;

public class SubscriptionsTest extends BaseIntegrationTest {

  @Autowired
  @Getter
  SubscriptionsService subService;

  @Before
  public void before() throws Exception {
    super.before();
    TimeUnit.SECONDS.sleep(2);
  }

  private void subscribeAll() throws Exception {
    for (User u : getMockUsers()) {
      if (u.getSession().getSessionToken().equals(getSession())) {
        for (Series s : getMockSeries()) {
          JsonNode response = mvcResultAsJson(
              getMockMvc()
                  .perform(MockMvcRequestBuilders.post("/api/v1/subscriptions?id=" + s.getId())
                      .header(Constants.AUTHORIZATION, Constants.BEARER + getSession()))
                  .andExpect(MockMvcResultMatchers.status().isOk()).andReturn()
          );
        }
      }
    }
  }

  @Test
  public void createSubscription() throws Exception {
    subscribeAll();
  }

  @Test
  public void deleteSubscription() throws Exception {
    for (User u : getMockUsers()) {
      if (u.getSession().getSessionToken().equals(getSession())) {
        for (Series s : getMockSeries()) {
          JsonNode response = mvcResultAsJson(
              getMockMvc()
                  .perform(MockMvcRequestBuilders.delete("/api/v1/subscriptions?id=" + s.getId())
                      .header(Constants.AUTHORIZATION, Constants.BEARER + getSession()))
                  .andExpect(MockMvcResultMatchers.status().isOk()).andReturn()
          );
        }
      }
    }
  }

  @After
  public void cleanup() throws Exception {
    for (User u : getMockUsers()) {
      if (u.getSession().getSessionToken().equals(getSession())) {
        for (Series s : getMockSeries()) {
          try {
            subService.deleteSubscription(u, s);
          } catch (Exception e) {

          }
        }
      }
    }
    super.cleanup();
  }
}