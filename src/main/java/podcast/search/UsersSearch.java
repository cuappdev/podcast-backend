package podcast.search;

import podcast.models.entities.User;
import java.util.List;

/** Abstract parent of all user search implementations **/
public abstract class UsersSearch {

  /** Given a query, search and return resultant users **/
  public abstract List<User> searchUsers(String query, Integer pageSize, Integer page);

}
