package me.itzg.kidsbank.users;

import java.security.Principal;

/**
 * @author Geoff Bourne
 * @since Oct 2018
 */
public class ParentPrincipal implements Principal {

  private String name;

  public ParentPrincipal(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }
}
