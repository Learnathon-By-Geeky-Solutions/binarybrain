package com.binarybrain.user.config;

import com.binarybrain.user.model.Role;
import com.binarybrain.user.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        createRoleIfNotExist("ADMIN");
        createRoleIfNotExist("TEACHER");
        createRoleIfNotExist("STUDENT");
    }

    private void createRoleIfNotExist(String roleName){
        roleRepository.findByName(roleName)
                .orElseGet(() ->
                        roleRepository.save(new Role(roleName))
                );
    }
}
