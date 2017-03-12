package podcast.search;

import podcast.models.entities.Person;
import java.util.List;

/** Abstract parent of all user search implementations **/
public abstract class UsersSearch {

  /** Given a query, search and return resultant users **/
  public abstract List<Person> searchUsers(String query, Integer offset, Integer max);

}
