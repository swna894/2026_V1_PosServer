package com.swna.server.user;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swna.server.common.response.ApiResponse;
import com.swna.server.user.dto.SignupRequest;
import com.swna.server.user.dto.UserRecordDto;
import com.swna.server.user.entity.model.Address;
import com.swna.server.user.entity.model.ContactInfo;
import com.swna.server.user.entity.model.Role;
import com.swna.server.user.entity.model.User;
import com.swna.server.user.security.UserPrincipal;
import com.swna.server.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ==========================================
    // Create
    // ==========================================


    /**
     * 사용자 신규 등록 (POST /users)
     * UserApiClient.createUser 대응
     */
    @PostMapping
    public ApiResponse<UserRecordDto> create(@RequestBody UserRecordDto request) {
        log.info("[API] POST /users - Creating user with email: {}", request.email());
        
        // 서비스에서 중복 검증 및 생성, DTO 변환을 일괄 처리
        UserRecordDto createdUser = userService.createUserFromDto(request);

        return ApiResponse.success(createdUser);
    }

    /**
     * 회원가입 (POST /users/signup)
     */
    @PostMapping("/signup")
    public ApiResponse<Void> signup(@RequestBody SignupRequest req) {
        userService.signup(req.email(), req.password());
        return ApiResponse.success(null);
    }

    // ==========================================
    // Read
    // ==========================================

    /**
     * 사용자 단건 조회 (ID) (GET /users/{id})
     * UserApiClient.getUserById 대응
     */
    @GetMapping("/{id}")
    public ApiResponse<UserRecordDto> getById(@NonNull @PathVariable Long id) {
        User user = userService.findUserById(id);
        return ApiResponse.success(UserRecordDto.from(user));
    }

    /**
     * 사용자 단건 조회 (이메일) (GET /users/email/{email})
     * UserApiClient.getUserByEmail 대응
     */
    @GetMapping("/email/{email}")
    public ApiResponse<UserRecordDto> getByEmail(@PathVariable String email) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
        return ApiResponse.success(UserRecordDto.from(user));
    }

    /**
     * 전체 사용자 목록 조회 (GET /users)
     * UserApiClient.getAllUsers 대응
     */
    @GetMapping
    public ApiResponse<List<UserRecordDto>> getAllUsers() {
        List<UserRecordDto> list = userService.findAll().stream()
                .map(UserRecordDto::from)
                .collect(Collectors.toList());
        return ApiResponse.success(list);
    }

    /**
     * 권한(Role)별 사용자 목록 조회 (GET /users/role/{role})
     * UserApiClient.getUsersByRole 대응
     */
    @GetMapping("/role/{role}")
    public ApiResponse<List<UserRecordDto>> getUsersByRole(@PathVariable String role) {
        Role targetRole = Role.valueOf(role.toUpperCase());
        List<UserRecordDto> list = userService.findByRole(targetRole).stream()
                .map(UserRecordDto::from)
                .collect(Collectors.toList());
        return ApiResponse.success(list);
    }

    // ==========================================
    // Update
    // ==========================================

    /**
     * 사용자 정보 수정 (PUT /users/{id})
     * UserApiClient.updateUser 대응
     */
    @PutMapping("/{id}")
    public ApiResponse<UserRecordDto> updateUser(@PathVariable Long id, @RequestBody UserRecordDto request) {
        User user = userService.findUserById(id);
                
        user.updateAddress(new Address(request.city(), request.street(), request.surburb()));
        user.updateContact(new ContactInfo(request.phone(), request.mobile()));
        
        User savedUser = userService.saveUser(user);
        return ApiResponse.success(UserRecordDto.from(savedUser));
    }

    /**
     * 사용자 일괄 수정 (PUT /users/bulk)
     * UserApiClient.updateUsersBulk 대응
     */
    @PutMapping("/bulk")
    public ApiResponse<List<UserRecordDto>> updateUsersBulk(@RequestBody List<UserRecordDto> requests) {
        List<UserRecordDto> updatedList = requests.stream().map(req -> {
            if (req.id() == null) return null;
            try {
                User user = userService.findUserById(req.id());
                user.updateAddress(new Address(req.city(), req.street(), req.surburb()));
                user.updateContact(new ContactInfo(req.phone(), req.mobile()));
                User saved = userService.saveUser(user);
                return UserRecordDto.from(saved);
            } catch (Exception e) {
                return null;
            }
        }).filter(java.util.Objects::nonNull).toList();

        return ApiResponse.success(updatedList);
    }

    // ==========================================
    // Delete
    // ==========================================

    /**
     * 사용자 삭제 (DELETE /users/{id})
     * UserApiClient.deleteUser 대응
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.success(null);
    }

    /**
     * 사용자 일괄 삭제 (DELETE /users/bulk)
     * UserApiClient.deleteUsersBulk 대응
     */
    @DeleteMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Integer> deleteUsersBulk(@RequestBody List<Long> ids) {
        int deletedCount = 0;
        for (Long id : ids) {
            try {
                userService.deleteUser(id);
                deletedCount++;
            } catch (Exception e) {
                // 삭제 실패 항목 예외 처리
            }
        }
        return ApiResponse.success(deletedCount);
    }

    // ==========================================
    // 추가 보안 및 편의 엔드포인트
    // ==========================================

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/me")
    public ApiResponse<String> me() {
        return ApiResponse.success("OK");
    }

    @GetMapping("/me2")
    public ApiResponse<String> me2(@AuthenticationPrincipal UserPrincipal user) {
        return ApiResponse.success("userId = " + user.getUserId());
    }
}