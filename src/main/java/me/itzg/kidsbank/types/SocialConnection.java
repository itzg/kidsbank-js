package me.itzg.kidsbank.types;

import lombok.Data;

/**
 * @author Geoff Bourne
 * @since Sep 2017
 */
@Data
public class SocialConnection {
    final String provider;
    final String user;
    String profileImageUrl;
    String displayName;
}
