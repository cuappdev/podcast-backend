package utils;

import lombok.Getter;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

public class MockGoogleCreds {

  @Getter private String sub;
  @Getter private String email;
  @Getter private String picture;

  /** Used in tests cases, when iterating with
   * a loop variable i **/
  public MockGoogleCreds(int i) {
    this.sub = "" + i;
    this.email = "johnDoe@gmail.com";
    this.picture = "https://goo.gl/L37gfS";
  }

  /** Creds as JsonNode **/
  public JsonNode toJsonNode() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.convertValue(this, JsonNode.class);
  }

}
