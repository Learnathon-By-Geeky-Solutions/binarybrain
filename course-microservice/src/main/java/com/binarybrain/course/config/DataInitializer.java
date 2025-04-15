package com.binarybrain.course.config;

import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private final DataSource dataSource;

    public DataInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("OCM_course.sql"));
        }
    }
}
