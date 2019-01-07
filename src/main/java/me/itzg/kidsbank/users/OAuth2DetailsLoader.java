package me.itzg.kidsbank.users;

/**
 * @author Geoff Bourne
 * @since Dec 2018
 */
public interface OAuth2DetailsLoader {

  Object loadUserDetails(String provider, String userId);
}
