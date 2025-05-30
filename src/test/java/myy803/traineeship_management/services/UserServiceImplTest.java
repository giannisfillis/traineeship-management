package myy803.traineeship_management.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import myy803.traineeship_management.domainmodel.User;
import myy803.traineeship_management.mappers.UserMapper;

@SpringBootTest
@TestPropertySource(
  locations = "classpath:application.properties")
@AutoConfigureMockMvc
public class UserServiceImplTest {
	
	@Autowired
    private WebApplicationContext context;
	
	@Autowired
	private MockMvc mockMvc;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
    	mockMvc = MockMvcBuilders
  	          .webAppContextSetup(context)
  	          .build();
        user = new User();
        user.setUsername("john_doe");
        user.setPassword("plain_password");
    }

    @Test
    void testSaveUserShouldEncodePasswordAndSaveUser() {
        when(bCryptPasswordEncoder.encode("plain_password")).thenReturn("encoded_password");

        userService.saveUser(user);

        assertEquals("encoded_password", user.getPassword());
        verify(userMapper).save(user);
    }

    @Test
    void testIsUserPresentWhenUserExistsShouldReturnTrue() {
        when(userMapper.findByUsername("john_doe")).thenReturn(Optional.of(user));

        boolean result = userService.isUserPresent(user);

        assertTrue(result);
    }

	@Test
    void testIsUserPresentWhenUserDoesNotExistShouldReturnFalse() {
        when(userMapper.findByUsername("john_doe")).thenReturn(Optional.empty());

        boolean result = userService.isUserPresent(user);

        assertFalse(result);
    }

    @Test
    void testLoadUserByUsernameWhenUserExistsShouldReturnUserDetails() {
        when(userMapper.findByUsername("john_doe")).thenReturn(Optional.of(user));

        UserDetails result = userService.loadUserByUsername("john_doe");

        assertEquals(user, result);
    }

    @Test
    void testLoadUserByUsernameWhenUserNotFoundShouldThrowException() {
        when(userMapper.findByUsername("john_doe")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("john_doe");
        });
    }

    @Test
    void testFindByIdWhenUserExistsShouldReturnUser() {
        when(userMapper.findByUsername("john_doe")).thenReturn(Optional.of(user));

        User result = userService.findById("john_doe");

        assertEquals(user, result);
    }

    @Test
    void testFindByIdWhenUserNotFoundShouldThrowException() {
        when(userMapper.findByUsername("john_doe")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userService.findById("john_doe");
        });
    }
}
