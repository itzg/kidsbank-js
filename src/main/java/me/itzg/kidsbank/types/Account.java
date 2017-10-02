package me.itzg.kidsbank.types;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Geoff Bourne
 * @since Sep 2017
 */
@Document
@Data
public class Account {

    @Id
    String id;

    String name;
}
