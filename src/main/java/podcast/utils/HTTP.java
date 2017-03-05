package podcast.utils;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

/**
 * HTTP utility methods
 */
public class HTTP {

  /* For appropriate user agent values on GET requesting */
  private static String userAgentValue = "Mozilla/5.0 " +
    "(Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2";

  /**
   * GET request
   * @param uri - String URI
   * @return - JsonNode
   */
  public static JsonNode get(String uri) {
    try {
      /* Client & request setup */
      HttpClient client = HttpClientBuilder.create().build();
      HttpGet request = new HttpGet(uri);
      request.addHeader("User-Agent", userAgentValue);

      /* Make the request */
      HttpResponse response = client.execute(request);

      /* If this is bad */
      if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
        throw new Exception();
      }

      /* Stream */
      BufferedReader rd = new BufferedReader(
        new InputStreamReader(response.getEntity().getContent()));

      /* Build itunes.result string */
      StringBuffer result = new StringBuffer();
      String line;
      while ((line = rd.readLine()) != null) {
        result.append(line);
      }

      /* Generate JSON response */
      ObjectMapper mapper = new ObjectMapper();
      return mapper.readTree(result.toString());

    } catch (Exception e) {
      return null;
    }
  }


  /**
   * POST request based on URI and parameters
   * @param uri - String URI
   * @param params - NameValuePair
   * @return - JsonNode
   */
  public static JsonNode post (String uri, List<BasicNameValuePair> params) {
    try {
      /* HTTP client */
      CloseableHttpClient client = HttpClients.createDefault();

      /* POST URL */
      HttpPost post = new HttpPost(uri);

      /* Set form entity based on params */
      post.setEntity(new UrlEncodedFormEntity(params));

      /* Make the request */
      HttpResponse response = client.execute(post);

      /* Stream */
      BufferedReader rd = new BufferedReader(
        new InputStreamReader(response.getEntity().getContent()));

      /* Build a JsonNode */
      StringBuffer result = new StringBuffer();
      String line;
      while ((line = rd.readLine()) != null) {
        result.append(line);
      }
      ObjectMapper mapper = new ObjectMapper();
      return mapper.readTree(result.toString());
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }


}
