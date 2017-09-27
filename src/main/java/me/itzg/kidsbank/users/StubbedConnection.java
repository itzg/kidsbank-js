package me.itzg.kidsbank.users;

import lombok.extern.slf4j.Slf4j;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.UserProfile;

/**
 * @author Geoff Bourne
 * @since Sep 2017
 */
@Slf4j
public class StubbedConnection implements Connection {
    private final ConnectionKey connectionKey;
    private final String displayName;
    private final String imageUrl;

    public StubbedConnection(String providerId, String providerUserId, String displayName, String imageUrl) {
        this.displayName = displayName;
        this.imageUrl = imageUrl;
        this.connectionKey = new ConnectionKey(providerId, providerUserId);
    }

    @Override
    public ConnectionKey getKey() {
        return connectionKey;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getProfileUrl() {
        return null;
    }

    @Override
    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public void sync() {

    }

    @Override
    public boolean test() {
        return true;
    }

    @Override
    public boolean hasExpired() {
        return false;
    }

    @Override
    public void refresh() {

    }

    @Override
    public UserProfile fetchUserProfile() {
        return UserProfile.EMPTY;
    }

    @Override
    public void updateStatus(String message) {

    }

    @Override
    public Object getApi() {
        return null;
    }

    @Override
    public ConnectionData createData() {
        return new ConnectionData(connectionKey.getProviderId(), connectionKey.getProviderUserId(),
                                  displayName, null, imageUrl, null, null, null, null);
    }
}
