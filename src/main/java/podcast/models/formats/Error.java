package podcast.models.formats;

import lombok.Getter;

/**
 * Error response format
 */
public class Error {

  @Getter private final String failure;

  /** Constructor **/
  public Error(String failure) {
    this.failure = failure;
  }

}
