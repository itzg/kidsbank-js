package me.itzg.kidsbank.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @author Geoff Bourne
 * @since Sep 2017
 */
@Document
@Data
public class Parent {
    @Id
    String id;

    List<SocialConnection> socialConnections;

}
