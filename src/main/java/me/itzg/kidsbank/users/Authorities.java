package me.itzg.kidsbank.users;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * @author Geoff Bourne
 * @since Sep 2017
 */
public class Authorities {
    public static final String PARENT = "ROLE_PARENT";
    public static final String ROLE_PARENT = "PARENT";
    public static final GrantedAuthority PARENT_AUTHORITY = new SimpleGrantedAuthority(PARENT);

    public static final String KID = "ROLE_KID";
    public static final String ROLE_KID = "KID";
    public static final GrantedAuthority KID_AUTHORITY = new SimpleGrantedAuthority(KID);
}
