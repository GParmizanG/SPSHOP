package com.mdtalalwasim.ecommerce.service;

import com.mdtalalwasim.ecommerce.entity.User;
import com.mdtalalwasim.ecommerce.repository.UserRepository;
import com.mdtalalwasim.ecommerce.service.impl.UserServiceImpl;
import com.mdtalalwasim.ecommerce.utils.AppConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl Unit Tests")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("rawPassword@1");
        testUser.setMobile("+79001234567");
    }

    // ── saveUser ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("saveUser: должен установить роль ROLE_USER, включить аккаунт и закодировать пароль")
    void saveUser_ShouldSetRoleEnableAndEncodePassword() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User saved = userService.saveUser(testUser);

        assertThat(saved.getRole()).isEqualTo("ROLE_USER");
        assertThat(saved.getIsEnable()).isTrue();
        assertThat(saved.getAccountStatusNonLocked()).isTrue();
        assertThat(saved.getAccountfailedAttemptCount()).isEqualTo(0);
        assertThat(saved.getAccountLockTime()).isNull();
        assertThat(saved.getPassword()).isEqualTo("encodedPassword");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("saveUser: при ошибке репозитория должен выбросить RuntimeException")
    void saveUser_WhenRepositoryFails_ShouldThrowRuntimeException() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("DB error"));

        assertThatThrownBy(() -> userService.saveUser(testUser))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to create user");
    }

    // ── getUserByEmail ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("getUserByEmail: должен вернуть пользователя по email")
    void getUserByEmail_ShouldReturnUser() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);

        User result = userService.getUserByEmail("test@example.com");

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("getUserByEmail: должен вернуть null если email не найден")
    void getUserByEmail_WhenNotFound_ShouldReturnNull() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);

        User result = userService.getUserByEmail("unknown@example.com");

        assertThat(result).isNull();
    }

    // ── existsByEmail ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("existsByEmail: должен вернуть true если email существует")
    void existsByEmail_WhenExists_ShouldReturnTrue() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThat(userService.existsByEmail("test@example.com")).isTrue();
    }

    @Test
    @DisplayName("existsByEmail: должен вернуть false если email не найден")
    void existsByEmail_WhenNotExists_ShouldReturnFalse() {
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);

        assertThat(userService.existsByEmail("new@example.com")).isFalse();
    }

    // ── getAllUsersByRole ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("getAllUsersByRole: должен вернуть список пользователей с указанной ролью")
    void getAllUsersByRole_ShouldReturnUserList() {
        when(userRepository.findByRole("ROLE_USER")).thenReturn(List.of(testUser));

        List<User> users = userService.getAllUsersByRole("ROLE_USER");

        assertThat(users).hasSize(1);
        assertThat(users.get(0).getEmail()).isEqualTo("test@example.com");
    }

    // ── updateUserStatus ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("updateUserStatus: должен вернуть true при успешном обновлении")
    void updateUserStatus_WhenUserExists_ShouldReturnTrue() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        Boolean result = userService.updateUserStatus(false, 1L);

        assertThat(result).isTrue();
        assertThat(testUser.getIsEnable()).isFalse();
    }

    @Test
    @DisplayName("updateUserStatus: должен вернуть false если пользователь не найден")
    void updateUserStatus_WhenUserNotFound_ShouldReturnFalse() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        Boolean result = userService.updateUserStatus(true, 99L);

        assertThat(result).isFalse();
    }

    // ── userFailedAttemptIncrease ──────────────────────────────────────────────────

    @Test
    @DisplayName("userFailedAttemptIncrease: должен увеличить счётчик неудачных попыток на 1")
    void userFailedAttemptIncrease_ShouldIncrementCounter() {
        testUser.setAccountfailedAttemptCount(1);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.userFailedAttemptIncrease(testUser);

        assertThat(testUser.getAccountfailedAttemptCount()).isEqualTo(2);
        verify(userRepository, times(1)).save(testUser);
    }

    // ── userAccountLock ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("userAccountLock: должен заблокировать аккаунт и установить время блокировки")
    void userAccountLock_ShouldLockAccountAndSetTime() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.userAccountLock(testUser);

        assertThat(testUser.getAccountStatusNonLocked()).isFalse();
        assertThat(testUser.getAccountLockTime()).isNotNull();
        verify(userRepository, times(1)).save(testUser);
    }

    // ── isUnlockAccountTimeExpired ─────────────────────────────────────────────────

    @Test
    @DisplayName("isUnlockAccountTimeExpired: должен разблокировать аккаунт если время истекло")
    void isUnlockAccountTimeExpired_WhenExpired_ShouldUnlockAndReturnTrue() {
        // Устанавливаем время блокировки в далёком прошлом (20 минут назад)
        long twentyMinutesAgo = System.currentTimeMillis() - (20 * 60 * 1000);
        testUser.setAccountLockTime(new Date(twentyMinutesAgo));
        testUser.setAccountStatusNonLocked(false);
        testUser.setAccountfailedAttemptCount(3);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        boolean result = userService.isUnlockAccountTimeExpired(testUser);

        assertThat(result).isTrue();
        assertThat(testUser.getAccountStatusNonLocked()).isTrue();
        assertThat(testUser.getAccountfailedAttemptCount()).isEqualTo(0);
        assertThat(testUser.getAccountLockTime()).isNull();
    }

    @Test
    @DisplayName("isUnlockAccountTimeExpired: должен вернуть false если время ещё не истекло")
    void isUnlockAccountTimeExpired_WhenNotExpired_ShouldReturnFalse() {
        // Блокировка была только что
        testUser.setAccountLockTime(new Date(System.currentTimeMillis()));
        testUser.setAccountStatusNonLocked(false);

        boolean result = userService.isUnlockAccountTimeExpired(testUser);

        assertThat(result).isFalse();
        assertThat(testUser.getAccountStatusNonLocked()).isFalse();
    }

    // ── updateUserResetToken ──────────────────────────────────────────────────────

    @Test
    @DisplayName("updateUserResetTokenForSendingEmail: должен обновить reset токен у пользователя")
    void updateUserResetToken_ShouldSetTokenAndSave() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.updateUserResetTokenForSendingEmail("test@example.com", "reset-token-123");

        assertThat(testUser.getResetTokens()).isEqualTo("reset-token-123");
        verify(userRepository, times(1)).save(testUser);
    }
}
