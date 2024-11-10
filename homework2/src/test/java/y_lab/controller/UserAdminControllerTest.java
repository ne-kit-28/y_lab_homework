package y_lab.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import y_lab.controller.admin.UserAdminController;
import y_lab.domain.User;
import y_lab.service.UserService;

import java.util.ArrayList;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

class UserAdminControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserAdminController userAdminController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userAdminController).build();
    }

    @Test
    @DisplayName("Get all users")
    void testGetUsers() throws Exception {
        User user = new User();
        user.setId(4L);
        ArrayList<User> users = new ArrayList<>(Collections.singletonList(user));
        when(userService.getUsers()).thenReturn(users);

        mockMvc.perform(get("/admin/user/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertThat(result.getResponse().getContentAsString()).contains("\"id\":4"));
    }

    @Test
    @DisplayName("Test block user")
    void testBlockUser() throws Exception {
        long userId = 1L;
        boolean status = true;

        when(userService.blockUser(eq(userId), eq(status))).thenReturn(true);

        mockMvc.perform(get("/admin/user/{userId}/block/{status}", userId, status))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Block user which doesn't exist")
    void testBlockUser_NotFound() throws Exception {
        long userId = 1L;
        boolean status = false;

        when(userService.blockUser(eq(userId), eq(status))).thenReturn(false);

        mockMvc.perform(get("/admin/user/{userId}/block/{status}", userId, status))
                .andExpect(status().isNotFound());
    }
}