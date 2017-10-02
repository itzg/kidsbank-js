package me.itzg.kidsbank.services;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 * @author Geoff Bourne
 * @since Sep 2017
 */
public class DefaultCodeGeneratorImplTest {

    @Test
    public void testTypical() {
        final DefaultCodeGeneratorImpl generator = new DefaultCodeGeneratorImpl(new Random(0));

        assertEquals("0897", generator.generate(4));
        assertEquals("531194", generator.generate(6));
    }
}