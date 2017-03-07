package podcast.models.entities;

import com.couchbase.client.java.document.json.JsonObject;
import lombok.Getter;
import java.util.UUID;

public class UserPair extends Entity {
    @Getter private UUID id1;
    @Getter private UUID id2;
    @Getter private PairType type;

    public UserPair(UUID id1, UUID id2, PairType type) {
        this.id1 = id1;
        this.id2 = id2;
        this.type = type;
    }

    public JsonObject toJsonObject() {
        // TODO
        return null;
    }
}
