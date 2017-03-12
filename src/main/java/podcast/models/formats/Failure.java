package podcast.models.formats;

import lombok.Getter;
import java.util.ArrayList;
import java.util.Arrays;
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

  /** Constructor with only one error **/
  public Failure(String error) {
    this.data = new HashMap<String, List<String>>();
    this.data.put("errors", new ArrayList<String>(Arrays.asList(error)));
  }

  /** Add an error **/
  public Failure addError(String error) {
    this.data.get("errors").add(error);
    return this;
  }

}
