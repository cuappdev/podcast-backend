package podcast.models.entities.users;

import com.couchbase.client.java.document.json.JsonObject;
import lombok.Getter;

import static podcast.utils.Constants.*;

/** Person scopes down the fields in User **/
public class Person {

  @Getter private String id;
  @Getter private String firstName;
  @Getter private String lastName;
  @Getter private String username;
  @Getter private Integer numberFollowers;
  @Getter private Integer numberFollowing;
  @Getter private String imageUrl;

  public Person(User user) {
    this.id = user.getId();
    this.firstName = user.getFirstName();
    this.lastName = user.getLastName();
    this.username = user.getUsername();
    this.numberFollowers = user.getNumberFollowers();
    this.numberFollowing = user.getNumberFollowing();
    this.imageUrl = user.getImageUrl();
  }

  public Person(JsonObject object) {
    this.id = object.getString(ID);
    this.firstName = object.getString(FIRST_NAME);
    this.lastName = object.getString(LAST_NAME);
    this.username = object.getString(USERNAME);
    this.numberFollowers = object.getInt(NUMBER_FOLLOWERS);
    this.numberFollowing = object.getInt(NUMBER_FOLLOWING);
    this.imageUrl = object.getString(IMAGE_URL);
  }

}
