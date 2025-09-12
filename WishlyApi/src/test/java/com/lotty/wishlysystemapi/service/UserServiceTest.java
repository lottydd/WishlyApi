//package com.lotty.wishlysystemapi.service;
//
//import com.lotty.wishlysystemapi.dto.request.RequestIdDTO;
//import com.lotty.wishlysystemapi.dto.request.user.UserCreateDTO;
//import com.lotty.wishlysystemapi.dto.request.user.UserUpdateDTO;
//import com.lotty.wishlysystemapi.dto.response.item.ItemResponseDTO;
//import com.lotty.wishlysystemapi.dto.response.user.UserCreateResponseDTO;
//import com.lotty.wishlysystemapi.dto.response.user.UserResponseDTO;
//import com.lotty.wishlysystemapi.dto.response.user.UserUpdateResponseDTO;
//import com.lotty.wishlysystemapi.mapper.ItemMapper;
//import com.lotty.wishlysystemapi.mapper.UserMapper;
//import com.lotty.wishlysystemapi.model.Item;
//import com.lotty.wishlysystemapi.model.Role;
//import com.lotty.wishlysystemapi.model.User;
//import com.lotty.wishlysystemapi.repository.RoleDAO;
//import com.lotty.wishlysystemapi.repository.UserDAO;
//import jakarta.persistence.EntityNotFoundException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.*;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
//class UserServiceTest {
//
//    @Mock private UserDAO userDAO;
//    @Mock private UserMapper userMapper;
//    @Mock private RoleDAO roleDAO;
//    @Mock private ItemMapper itemMapper;
//    @Mock private PasswordEncoder passwordEncoder;
//
//    @InjectMocks private UserService userService;
//
//    private User user;
//    private Role role;
//
//    @BeforeEach
//    void setUp() {
//        user = new User();
//        user.setUserId(1);
//        user.setUsername("john");
//        user.setEmail("john@example.com");
//
//        role = new Role(1, "ROLE_USER");
//    }
//
//    @Test
//    void createUser_success_assignsRole() {
//        UserCreateDTO dto = new UserCreateDTO();
//        dto.setEmail("a@b.com");
//        dto.setUsername("alex");
//        dto.setPassword("pass");
//
//        when(userDAO.existsByEmail("a@b.com")).thenReturn(false);
//        when(userDAO.existsByUsername("alex")).thenReturn(false);
//
//        User mapped = new User();
//        mapped.setUserId(1);
//        mapped.setUsername("alex");
//
//        when(userMapper.toEntity(dto, passwordEncoder)).thenReturn(mapped);
//
//        when(userDAO.save(mapped)).thenReturn(mapped);
//
//        when(userDAO.findById(1)).thenReturn(Optional.of(mapped));
//
//        Role role = new Role(1, "ROLE_USER");
//        when(roleDAO.findRoleName("ROLE_USER")).thenReturn(Optional.of(role));
//
//
//        UserCreateResponseDTO resp = new UserCreateResponseDTO();
//        resp.setUsername("alex");
//        when(userMapper.toUserCreateResponseDTO(mapped)).thenReturn(resp);
//
//        UserCreateResponseDTO out = userService.createUser(dto);
//
//        assertThat(out).isNotNull();
//        assertThat(out.getUsername()).isEqualTo("alex");
//
//        verify(userDAO, atLeastOnce()).save(mapped);
//        verify(roleDAO).findRoleName("ROLE_USER");
//    }
//
//
//
//    @Test
//    void createUser_emailTaken_throws() {
//        UserCreateDTO dto = new UserCreateDTO();
//        dto.setEmail("taken@example.com");
//        dto.setUsername("new");
//
//        when(userDAO.existsByEmail("taken@example.com")).thenReturn(true);
//
//        assertThatThrownBy(() -> userService.createUser(dto))
//                .isInstanceOf(IllegalArgumentException.class);
//        verify(userDAO, never()).save(any());
//    }
//
//    @Test
//    void assignRoleToUser_success() {
//        when(userDAO.findById(1)).thenReturn(Optional.of(user));
//        when(roleDAO.findRoleName("ROLE_ADMIN")).thenReturn(Optional.of(role));
//        user.getRoles().clear();
//
//        UserUpdateResponseDTO dto = new UserUpdateResponseDTO();
//        when(userMapper.toUserUpdateResponseDTO(user)).thenReturn(dto);
//
//        UserUpdateResponseDTO result = userService.assignRoleToUser(1, "ROLE_ADMIN");
//        assertThat(result).isNotNull();
//        verify(userDAO).save(user);
//    }
//
//    @Test
//    void assignRoleToUser_roleNotFound_throws() {
//        when(userDAO.findById(1)).thenReturn(Optional.of(user));
//        when(roleDAO.findRoleName("ROLE_X")).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> userService.assignRoleToUser(1, "ROLE_X"))
//                .isInstanceOf(EntityNotFoundException.class);
//    }
//
//    @Test
//    void assignRoleToUser_alreadyHasRole_throws() {
//        user.getRoles().add(role);
//        when(userDAO.findById(1)).thenReturn(Optional.of(user));
//        when(roleDAO.findRoleName("ROLE_USER")).thenReturn(Optional.of(role));
//
//        assertThatThrownBy(() -> userService.assignRoleToUser(1, "ROLE_USER"))
//                .isInstanceOf(IllegalArgumentException.class);
//    }
//
//    @Test
//    void deleteRoleFromUser_success() {
//        user.getRoles().add(role);
//        when(userDAO.findById(1)).thenReturn(Optional.of(user));
//        when(roleDAO.findRoleName("ROLE_USER")).thenReturn(Optional.of(role));
//        when(userMapper.toUserUpdateResponseDTO(user)).thenReturn(new UserUpdateResponseDTO());
//
//        UserUpdateResponseDTO resp = userService.deleteRoleFromUser(1, "ROLE_USER");
//        assertThat(resp).isNotNull();
//        verify(userDAO).save(user);
//    }
//
//    @Test
//    void changePassword_success() {
//        when(userDAO.findById(1)).thenReturn(Optional.of(user));
//        when(passwordEncoder.encode("newpass")).thenReturn("encoded");
//        userService.changePassword(1, "newpass");
//        assertThat(user.getPassword()).isEqualTo("encoded");
//        verify(userDAO).save(user);
//    }
//
//    @Test
//    void changePassword_userNotFound_throws() {
//        when(userDAO.findById(5)).thenReturn(Optional.empty());
//        assertThatThrownBy(() -> userService.changePassword(5, "p"))
//                .isInstanceOf(EntityNotFoundException.class);
//    }
//
//    @Test
//    void updateUser_success() {
//        UserUpdateDTO dto = new UserUpdateDTO();
//        dto.setUsername("updated");
//        dto.setEmail("updated@example.com");
//
//        when(userDAO.findById(1)).thenReturn(Optional.of(user));
//        // nothing taken by others
//        when(userDAO.findByEmail("updated@example.com")).thenReturn(Optional.empty());
//        when(userDAO.findByUsername("updated")).thenReturn(Optional.empty());
//
//        doAnswer(invocation -> {
//            // mimic mapper update
//            UserUpdateDTO passed = invocation.getArgument(0);
//            User target = invocation.getArgument(1);
//            target.setUsername(passed.getUsername());
//            target.setEmail(passed.getEmail());
//            return null;
//        }).when(userMapper).updateUserFromDto(dto, user);
//
//        when(userDAO.save(user)).thenReturn(user);
//        when(userMapper.toUserUpdateResponseDTO(user)).thenReturn(new UserUpdateResponseDTO());
//
//        UserUpdateResponseDTO result = userService.updateUser(1, dto);
//        assertThat(result).isNotNull();
//        verify(userDAO).save(user);
//    }
//
//    @Test
//    void findUserById_success() {
//        when(userDAO.findById(1)).thenReturn(Optional.of(user));
//        when(userMapper.toUserResponseDTO(user)).thenReturn(new UserResponseDTO());
//        UserResponseDTO resp = userService.findUserById(new RequestIdDTO(1));
//        assertThat(resp).isNotNull();
//    }
//
//    @Test
//    void findUserById_notFound_throws() {
//        when(userDAO.findById(45)).thenReturn(Optional.empty());
//        assertThatThrownBy(() -> userService.findUserById(new RequestIdDTO(45)))
//                .isInstanceOf(EntityNotFoundException.class);
//    }
//
//    @Test
//    void getUserItems_returnsList() {
//        ItemResponseDTO it = new ItemResponseDTO();
//        when(userDAO.findById(1)).thenReturn(Optional.of(user));
//        user.getOwnedItems().add(new Item());
//        when(itemMapper.toItemResponseDTOList(user.getOwnedItems())).thenReturn(List.of(it));
//
//        var list = userService.getUserItems(1);
//        assertThat(list).hasSize(1);
//    }
//}
