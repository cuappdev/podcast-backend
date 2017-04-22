package podcast.models.entities.followings;

import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import podcast.models.entities.Entity;
import podcast.models.entities.users.User;

import static podcast.utils.Constants.*;

/**
 * Follower (someone following you)
 */
public class Follower extends FollowRelationship {

  /**
   * Constructor from owning User and future follower
   * @param owner - User
   * @param follower - User
   */
  public Follower(User owner, User follower) {
    super(
      Type.follower,
      owner.getId(),
      follower.getId(),
      follower.getFirstName(),
      follower.getLastName(),
      follower.getUsername(),
      follower.getImageUrl());
  }

  /** Constructor from JsonObject **/
  public Follower(JsonObject object) {
    super(
      Type.follower,
      object.getString(OWNER_ID),
      object.getString(ID),
      object.getString(FIRST_NAME),
      object.getString(LAST_NAME),
      object.getString(USERNAME),
      object.getString(IMAGE_URL)
    );
  }

  /** See {@link Entity#toJsonDocument()} **/
  public JsonDocument toJsonDocument() {
    return super.toJsonDocument(composeKey(this));
  }

  /** Compose key from ownerId and correspondentId **/
  public static String composeKey(String ownerId, String corrId) {
    return Entity.composeKey(String.format("%s:%s", ownerId, corrId), Type.follower.toString());
  }

  /** Compose key from owner and correspondent **/
  public static String composeKey(User owner, User correspondent) {
    return composeKey(owner.getId(), correspondent.getId());
  }

  /** Compose key from follower **/
  public static String composeKey(Follower f) {
    return composeKey(f.getOwnerId(), f.getId());
  }

}
