package utils;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import podcast.configs.AppConfig;

/** Base class for testing **/
@SpringBootTest(classes = AppConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public abstract class BaseTest {

  private ObjectMapper mapper = new ObjectMapper();

  /** MvcResult -> JsonNode **/
  public JsonNode mvcResultAsJson(MvcResult result) throws Exception {
    return mapper.readTree(result.getResponse().getContentAsString());
  }

}
