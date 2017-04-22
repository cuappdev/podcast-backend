package podcast.models.entities.users;

import com.couchbase.client.java.document.json.JsonObject;
import lombok.Getter;

import static podcast.utils.Constants.*;

/** User data nested in a model that requires some User data **/
public class AssociatedUser {

  @Getter
  private String id;
  @Getter private String firstName;
  @Getter private String lastName;
  @Getter private String username;
  @Getter private String imageUrl;

  public AssociatedUser(User user) {
    this.id = user.getId();
    this.firstName = user.getFirstName();
    this.lastName = user.getLastName();
    this.username = user.getUsername();
    this.imageUrl = user.getImageUrl();
  }

  public AssociatedUser(JsonObject object) {
    this.id = object.getString(ID);
    this.firstName = object.getString(FIRST_NAME);
    this.lastName = object.getString(LAST_NAME);
    this.username = object.getString(USERNAME);
    this.imageUrl = object.getString(IMAGE_URL);
  }
}
