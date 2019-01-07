package me.itzg.kidsbank.types;

import java.util.List;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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

    List<String> accounts;
}
