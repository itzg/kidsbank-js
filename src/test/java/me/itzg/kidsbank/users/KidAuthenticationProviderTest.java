package me.itzg.kidsbank.users;

import me.itzg.kidsbank.config.KidsbankProperties;
import me.itzg.kidsbank.services.KidlinkService;
import me.itzg.kidsbank.types.KidLogin;
import me.itzg.kidsbank.types.KidRegistration;
import me.itzg.kidsbank.types.Kidlink;
import me.itzg.kidsbank.types.MongoIndexes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Geoff Bourne
 * @since Oct 2017
 */
@RunWith(SpringRunner.class)
@DataMongoTest
public class KidAuthenticationProviderTest {

    public static final String ENCODED_PREFIX = "encoded-";

    @TestConfiguration
    @Import({KidAuthenticationProvider.class, MongoIndexes.class})
    public static class Config {
        @Bean
        public KidsbankProperties kidsbankProperties() {
            final KidsbankProperties properties = new KidsbankProperties();
            properties.setKidlinkExpiration(2);
            return properties;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new PasswordEncoder() {
                @Override
                public String encode(CharSequence rawPassword) {
                    return ENCODED_PREFIX + String.valueOf(rawPassword);
                }

                @Override
                public boolean matches(CharSequence rawPassword, String encodedPassword) {
                    return encodedPassword.equals("encoded-" + String.valueOf(rawPassword));
                }
            };
        }
    }

    @MockBean
    KidlinkService kidlinkService;

    @Autowired
    KidAuthenticationProvider kidAuthenticationProvider;

    @Test
    public void testNormalRegistration() {
        final KidRegistration registration = new KidRegistration();
        registration.setUsername("newuser");
        registration.setPassword("password");
        registration.setKidlinkCode("1234");
        final KidAuthenticationToken auth = new KidAuthenticationToken(registration);

        final Kidlink kidlink = new Kidlink();
        kidlink.setSharedBy("parent-1");
        kidlink.setAccounts(Collections.singletonList("account-1"));

        Mockito.when(kidlinkService.useCode("1234"))
                .thenReturn(kidlink);

        final Authentication authenticated = kidAuthenticationProvider.authenticate(auth);
        Mockito.verify(kidlinkService).useCode("1234");

        assertEquals("newuser", authenticated.getName());
        final Object principal = authenticated.getPrincipal();
        assertTrue(principal instanceof AuthenticatedKid);
        final AuthenticatedKid authenticatedKid = (AuthenticatedKid) principal;
        assertEquals(ENCODED_PREFIX + "password", authenticatedKid.getKid().getEncPassword());
    }

    @Test
    public void testRegisterButUsernameTaken() {
        final KidRegistration registration = new KidRegistration();
        registration.setUsername("testRegisterButUsernameTaken");
        registration.setPassword("password");
        registration.setKidlinkCode("1234");
        final KidAuthenticationToken auth = new KidAuthenticationToken(registration);

        final Kidlink kidlink = new Kidlink();
        kidlink.setSharedBy("parent-1");
        kidlink.setAccounts(Collections.singletonList("account-1"));

        Mockito.when(kidlinkService.useCode("1234"))
                .thenReturn(kidlink);

        final Authentication authenticated = kidAuthenticationProvider.authenticate(auth);
        assertNotNull(authenticated);
        Mockito.verify(kidlinkService).useCode("1234");

        final KidRegistration dupeRegistration = new KidRegistration();
        dupeRegistration.setUsername("testRegisterButUsernameTaken");
        dupeRegistration.setKidlinkCode("2345");
        final KidAuthenticationToken dupeAuth = new KidAuthenticationToken(dupeRegistration);

        try {
            kidAuthenticationProvider.authenticate(dupeAuth);
            fail("Should have failed authentication");
        } catch (AuthenticationException e) {
            Mockito.verifyNoMoreInteractions(kidlinkService);
        }
    }

    @Test
    public void testNormalLogin() {
        final KidRegistration registration = new KidRegistration();
        registration.setUsername("testNormalLogin");
        registration.setPassword("password");
        registration.setKidlinkCode("1234");
        final KidAuthenticationToken auth = new KidAuthenticationToken(registration);

        final Kidlink kidlink = new Kidlink();
        kidlink.setSharedBy("parent-1");
        kidlink.setAccounts(Collections.singletonList("account-1"));

        Mockito.when(kidlinkService.useCode("1234"))
                .thenReturn(kidlink);

        final Authentication authenticatedRegister = kidAuthenticationProvider.authenticate(auth);
        assertNotNull(authenticatedRegister);
        Mockito.verify(kidlinkService).useCode("1234");

        final KidLogin login = new KidLogin();
        login.setUsername("testNormalLogin");
        login.setPassword("password");

        final Authentication authenticated = kidAuthenticationProvider.authenticate(new KidAuthenticationToken(login));
        assertNotNull(authenticated);
    }

    @Test(expected = BadCredentialsException.class)
    public void testLoginWrongPassword() {
        final KidRegistration registration = new KidRegistration();
        registration.setUsername("testLoginWrongPassword");
        registration.setPassword("password");
        registration.setKidlinkCode("1234");
        final KidAuthenticationToken auth = new KidAuthenticationToken(registration);

        final Kidlink kidlink = new Kidlink();
        kidlink.setSharedBy("parent-1");
        kidlink.setAccounts(Collections.singletonList("account-1"));

        Mockito.when(kidlinkService.useCode("1234"))
                .thenReturn(kidlink);

        final Authentication authenticatedRegister = kidAuthenticationProvider.authenticate(auth);
        assertNotNull(authenticatedRegister);
        Mockito.verify(kidlinkService).useCode("1234");

        final KidLogin login = new KidLogin();
        login.setUsername("testLoginWrongPassword");
        login.setPassword("WRONG");

        kidAuthenticationProvider.authenticate(new KidAuthenticationToken(login));
    }

    @Test(expected = BadCredentialsException.class)
    public void testLoginUnknownUser() {
        final KidLogin login = new KidLogin();
        login.setUsername("testLoginUnknownUser");
        login.setPassword("N/A");

        kidAuthenticationProvider.authenticate(new KidAuthenticationToken(login));
    }
}