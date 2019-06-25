package ru.x5.motpsender.dao;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SessionInfo {
    private ThreadLocal<String> userInn = new ThreadLocal<>();
    private ThreadLocal<UUID> globalUUID = new ThreadLocal<>();

    public void setUserInn(String userInn) {
        this.userInn.set(userInn);
    }

    public String getUserInn() {
        return this.userInn.get();
    }

    public UUID getGlobalUUID() {
        return this.globalUUID.get();
    }

    public void setGlobalUUID(UUID globalUUID) {
        this.globalUUID.set(globalUUID);
    }

    public void setGlobalUUID(String globalUUID) {
        this.globalUUID.set(UUID.fromString(globalUUID));
    }
}
