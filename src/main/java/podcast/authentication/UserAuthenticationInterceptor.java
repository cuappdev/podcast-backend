package podcast.authentication;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.error.DocumentDoesNotExistException;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import podcast.models.entities.Session;
import podcast.models.entities.User;
import podcast.models.utils.Constants;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.NotAuthorizedException;
import java.util.Date;

public class UserAuthenticationInterceptor extends HandlerInterceptorAdapter {

  private Bucket bucket;

  public UserAuthenticationInterceptor(Bucket bucket) {
    this.bucket = bucket;
  }

  @Override
  public void afterCompletion(HttpServletRequest request,
                              HttpServletResponse response,
                              Object handler,
                              Exception exception)
    throws Exception {
  }

  @Override
  public void postHandle(HttpServletRequest request,
                         HttpServletResponse response,
                         Object handler,
                         ModelAndView modelAndView)
    throws Exception {
  }

  @Override
  public boolean preHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler) throws Exception {

    /* Get the HTTP Authorization header from the request */
    String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

    /* Ensure that the HTTP Authorization header is present and
     * properly formatted */
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
      response.sendError(
        HttpServletResponse.SC_FORBIDDEN,
        "Please provide a properly formatted sessionToken");
      return false;
    }

    /* Grab session token from header */
    String sessionToken = authorizationHeader.substring("Bearer".length()).trim();

    try {
      /* Grab user + set attribute */
      request.setAttribute(Constants.USER, validateSessionToken(sessionToken));
    } catch (Exception e) {
      /* Send an error */
      response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
      return false;
    }

    return true;
  }



  /** Validate session token **/
  public User validateSessionToken(String sessionToken) throws Exception {
    User user; // To be populated in try-block
    try {
      /* Attempt to get the user */
      String userId = Session.tokenToUserId(sessionToken);
      JsonDocument result = bucket.get(userId);
      user = new User(result.content());

      /* Check session equality */
      if (!user.getSession().getSessionToken().equals(sessionToken)) {
        throw new InvalidSessionException();
      }

      /* Check expiration date */
      if (user.getSession().getExpiresAt().before(new Date())) {
        throw new ExpiredSessionException();
      }

    }
    /* Caught if document with above userId is not found */
    catch (Exception e) {
      throw new InvalidSessionException();
    }

    /* Return resultant user if successful */
    return user;
  }


  /** When authentication flow reveals a sessionToken has expired **/
  public class ExpiredSessionException extends Exception {
    public ExpiredSessionException() {
      super("This sessionToken has expired.  Please request a new one.");
    }
  }


  /** When a session does not correspond to a user or when the session
   * token is completely wrong **/
  public class InvalidSessionException extends Exception {
    public InvalidSessionException() {
      super("This sessionToken is invalid.");
    }
  }

}
