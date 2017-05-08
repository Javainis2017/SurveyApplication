package com.javainis.user_management.dao;

import com.javainis.user_management.entities.User;
import com.javainis.user_management.entities.UserType;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserDAOTest {

    Long userId = 100l;

    @Mock
    DataSource mockDataSource;

    @Mock
    Connection mockConn;

    @Mock
    private UserDAO userDAO;
    private User user;
    private User mockedUser;

    public UserDAOTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws SQLException {
        when(mockDataSource.getConnection()).thenReturn(mockConn);
        when(mockDataSource.getConnection(anyString(), anyString())).thenReturn(mockConn);
        doNothing().when(mockConn).commit();
        user = new User();
    }

    @After
    public void tearDown() {
    }

    @Test(expected = IllegalArgumentException.class)
    public void userCreationWithWrongValues() throws  Exception
    {
        user.setEmail(null);
        user.setPasswordHash(null);
        user.setBlocked(false);
        user.setUserID(userId);
        user.setFirstName("Tom");
        user.setLastName("Hogwards");
        user.setUserType(new UserType());
        userDAO.create(user);
    }

    @Test
    public void userCreationTest_CreatesUser() throws Exception {
        user.setEmail("c@c.lt");
        user.setPasswordHash("a");
        user.setBlocked(false);
        user.setUserID(userId);
        user.setFirstName("Tom");
        user.setLastName("Hogwards");
        user.setUserType(new UserType());
        userDAO.create(user);
        User userTest = userDAO.getUserByEmail("c@c.lt");
        assertEquals("c@c.lt", userTest.getEmail());
        verify(userDAO, Mockito.times(1)).getUserByEmail(anyString());
    }


}