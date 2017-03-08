package podcast.models.formats;

import lombok.Getter;
import java.util.HashMap;
import java.util.List;

/**
 * Failure response format
 */
public class Failure extends Result {

  @Getter private Boolean success = false;
  @Getter private HashMap<String, List<String>> data;

  /** Constructor **/
  public Failure(List<String> errors) {
    this.data = new HashMap<String, List<String>>();
    this.data.put("errors", errors);
  }

  /** Add an error **/
  public void addError(String error) {
    this.data.get("errors").add(error);
  }

}
