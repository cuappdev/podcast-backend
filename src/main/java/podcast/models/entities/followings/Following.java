package podcast.models.entities.followings;

import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import podcast.models.entities.Entity;
import podcast.models.entities.users.User;

import static podcast.utils.Constants.*;

/**
 * Following (someone you follow)
 */
public class Following extends FollowRelationship {

  /**
   * Constructor from owning User and future following
   * @param owner - User
   * @param following - User
   */
  public Following(User owner, User following) {
    super(
      Type.following,
      owner.getId(),
      following.getId(),
      following.getFirstName(),
      following.getLastName(),
      following.getUsername(),
      following.getImageUrl());
  }

  /** Constructor from JsonObject **/
  public Following(JsonObject object) {
    super(
      Type.following,
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
    return Entity.composeKey(String.format("%s:%s", ownerId, corrId), Type.following.toString());
  }

  /** Compose key from owner and correspondent **/
  public static String composeKey(User owner, User correspondent) {
    return composeKey(owner.getId(), correspondent.getId());
  }

  /** Compose key from following **/
  public static String composeKey(Following f) {
    return composeKey(f.getOwnerId(), f.getId());
  }

}
