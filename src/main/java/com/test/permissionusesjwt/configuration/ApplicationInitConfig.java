package com.test.permissionusesjwt.configuration;

import com.test.permissionusesjwt.constant.DefinedRole;
import com.test.permissionusesjwt.entity.Role;
import com.test.permissionusesjwt.entity.User;
import com.test.permissionusesjwt.repository.UserRepository;
import com.test.permissionusesjwt.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {


    PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository
    , RoleRepository roleRepository) {
        return args -> {
            if(userRepository.findByUsername("admin").isEmpty()) {
                var roles = new HashSet<Role>();
                Role adminRole = roleRepository.save(Role.builder()
                        .name(DefinedRole.ADMIN_ROLE)
                        .description("Admin role")
                        .build());
                roles.add(adminRole);

                User user = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        .roles(roles)
                        .build();
                userRepository.save(user);
                log.warn("admin user has been created with default password: admin, please change it ");
            }
        };
    }

}
