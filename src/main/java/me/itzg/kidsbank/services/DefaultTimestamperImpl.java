package me.itzg.kidsbank.services;

import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author Geoff Bourne
 * @since Sep 2017
 */
@Service
public class DefaultTimestamperImpl implements Timestamper {
    @Override
    public Date now() {
        return new Date();
    }
}
