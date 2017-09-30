package me.itzg.kidsbank.types;

import lombok.Data;

/**
 * @author Geoff Bourne
 * @since Sep 2017
 */
@Data
public class Profile {

    String userId;
    String displayName;
    String profileImageUrl;
    Role role;
}
