package com.efs.modules.playbook.support;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class PlaybookTestDataCleaner {

    private final Environment environment;

    public PlaybookTestDataCleaner(
            Environment environment
    ) {
        this.environment = environment;
    }

    public void clean() {
        String url = environment.getRequiredProperty(
                "spring.datasource.url"
        );

        String username = environment.getRequiredProperty(
                "spring.flyway.user"
        );

        String password = environment.getRequiredProperty(
                "spring.flyway.password"
        );

        try (
                Connection connection =
                        DriverManager.getConnection(
                                url,
                                username,
                                password
                        )
        ) {
            connection.setAutoCommit(false);

            try (Statement statement =
                         connection.createStatement()) {

                statement.executeUpdate(
                        "DELETE FROM " +
                        "playbook.playbook_execution_step"
                );

                statement.executeUpdate(
                        "DELETE FROM " +
                        "playbook.playbook_execution"
                );

                statement.executeUpdate(
                        "DELETE FROM " +
                        "playbook.playbook_step"
                );

                statement.executeUpdate(
                        "DELETE FROM " +
                        "playbook.playbook_version"
                );

                statement.executeUpdate(
                        "DELETE FROM " +
                        "playbook.playbook"
                );

                connection.commit();

            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            }

        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Failed to clean Playbook integration test data",
                    exception
            );
        }
    }
}