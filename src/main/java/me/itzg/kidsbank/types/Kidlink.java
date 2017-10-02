package me.itzg.kidsbank.types;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * @author Geoff Bourne
 * @since Sep 2017
 */
@Document
@Data
public class Kidlink {

    @Id
    String code;

    String sharedBy;

    Date created;

    List<String> accounts;
}
