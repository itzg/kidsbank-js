package me.itzg.kidsbank.repositories;

import me.itzg.kidsbank.domain.Parent;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Geoff Bourne
 * @since Sep 2017
 */
public interface ParentRepository extends CrudRepository<Parent, String> {
}
