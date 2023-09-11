package ru.jok1r.diverseLogger.diverse;

import java.util.UUID;

public class DroppedData {

    private final UUID uuid;
    private final Long droppedTime;
    private final String player;

    public DroppedData(UUID uuid, Long droppedTime, String player) {
        this.uuid = uuid;
        this.droppedTime = droppedTime;
        this.player = player;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public Long getDroppedTime() {
        return this.droppedTime;
    }

    public String getPlayer() {
        return this.player;
    }
}
