package com.project.dadn.components.datainit;

import com.project.dadn.models.Permission;
import com.project.dadn.models.Role;
import com.project.dadn.repositories.PermissionRepository;
import com.project.dadn.repositories.RoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class RoleInitializer {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @PostConstruct
    public void init() {
        if (roleRepository.count() == 0 && permissionRepository.count() == 0) {

            // Tạo các permission mặc định
            List<Permission> permissions = Stream.of(
                    Permission.builder().name("READ_PRIVILEGES").description("Allows reading data").build(),
                    Permission.builder().name("WRITE_PRIVILEGES").description("Allows writing data").build(),
                    Permission.builder().name("DELETE_PRIVILEGES").description("Allows deleting data").build(),
                    Permission.builder().name("UPDATE_PRIVILEGES").description("Allows updating data").build()
            ).collect(Collectors.toList());

            // Lưu các permission vào database
            permissionRepository.saveAll(permissions);

            // Chọn ngẫu nhiên 2 permission cho mỗi role
            Set<Permission> adminPermissions = new HashSet<>();
            adminPermissions.add(permissions.get(0));
            adminPermissions.add(permissions.get(1));

            Set<Permission> userPermissions = new HashSet<>();
            userPermissions.add(permissions.get(2));
            userPermissions.add(permissions.get(3));

            // Tạo các role với permissions
            Role adminRole = Role.builder()
                    .name("ADMIN")
                    .description("Admin roles")
                    .permissions(adminPermissions)
                    .build();

            Role userRole = Role.builder()
                    .name("USER")
                    .description("User roles")
                    .permissions(userPermissions)
                    .build();

            roleRepository.save(adminRole);
            roleRepository.save(userRole);

            System.out.println("Initialized roles: ADMIN, USER with permissions");
        }
    }




}

