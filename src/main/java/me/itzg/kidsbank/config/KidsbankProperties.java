package me.itzg.kidsbank.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Min;

/**
 * @author Geoff Bourne
 * @since Sep 2017
 */
@ConfigurationProperties("kidsbank")
@Component
@Data
public class KidsbankProperties {

    /**
     * How long kidlink codes are valid in seconds.
     */
    int kidlinkExpiration = 86_400;

    /**
     * The length of kidlink codes, where are numerical.
     */
    @Min(1)
    int kidlinkDigits = 4;
}
