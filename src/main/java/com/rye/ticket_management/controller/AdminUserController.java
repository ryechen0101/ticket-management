    package com.rye.ticket_management.controller;

    import com.rye.ticket_management.dto.admin.*;
    import com.rye.ticket_management.service.AdminUserService;
    import jakarta.validation.Valid;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;

    @RestController
    @RequestMapping("/api/admin/users")
    public class AdminUserController {

        private final AdminUserService adminUserService;

        public AdminUserController(AdminUserService adminUserService) {
            this.adminUserService = adminUserService;
        }

        // 列表
        @GetMapping
        public List<UserResponse> list() {
            return adminUserService.listUsers();
        }

        // 查單筆
        @GetMapping("/{id}")
        public UserResponse getOne(@PathVariable Long id) {
            return adminUserService.getUserById(id);
        }

        // 建立
        @PostMapping
        public UserResponse create(@Valid @RequestBody CreateUserRequest req) {
            return adminUserService.createUser(req);
        }

        // 改角色
        @PutMapping("/{id}/role")
        public UserResponse updateRole(@PathVariable Long id, @Valid @RequestBody UpdateRoleRequest req) {
            return adminUserService.updateRole(id, req);
        }

        // 改密碼
        @PutMapping("/{id}/password")
        public String updatePassword(@PathVariable Long id, @Valid @RequestBody UpdatePasswordRequest req) {
            adminUserService.updatePassword(id, req);
            return "密碼已更新";
        }

        // 啟用/停用
        @PatchMapping("/{id}/enabled")
        public UserResponse setEnabled(@PathVariable Long id, @RequestBody UpdateEnabledRequest req) {
            return adminUserService.setEnabled(id, req.enabled());
        }
    }
