package me.itzg.kidsbank.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * @author Geoff Bourne
 * @since Sep 2017
 */
@Service
public class DefaultCodeGeneratorImpl implements CodeGenerator {

    private final Random rand;

    @Autowired
    public DefaultCodeGeneratorImpl(Random rand) {
        this.rand = rand;
    }

    @Override
    public String generate(int digits) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digits; ++i) {
            sb.append(Integer.toString(rand.nextInt(10)));
        }

        return sb.toString();
    }
}
