package me.itzg.kidsbank.repositories;

import java.util.Optional;
import me.itzg.kidsbank.types.Parent;
import me.itzg.kidsbank.types.SocialConnection;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Geoff Bourne
 * @since Sep 2017
 */
public interface ParentRepository extends CrudRepository<Parent, String> {

  Optional<Parent> findBySocialConnectionsContains(SocialConnection socialConnection);

}
