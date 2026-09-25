package com.technomancarai.tms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@SpringBootTest
class DatabaseConnectionTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DataSource dataSource;

    @Test
    @DisplayName("Verify Spring ApplicationContext loads successfully")
    void testApplicationContextLoads() {
        assertThat(applicationContext)
                .as("ApplicationContext should not be null")
                .isNotNull();
        System.out.println("SUCCESS: Spring ApplicationContext loaded successfully.");
    }

    @Test
    @DisplayName("Verify DataSource bean is available and database connection can be established")
    void testDatabaseConnectionAndQueryExecution() {
        assertThat(dataSource)
                .as("DataSource bean should be registered in the Spring context")
                .isNotNull();

        try (Connection connection = dataSource.getConnection()) {
            assertThat(connection)
                    .as("Database connection should be established successfully")
                    .isNotNull();
            assertThat(connection.isClosed())
                    .as("Database connection should be open")
                    .isFalse();

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("SELECT 1")) {

                assertThat(resultSet.next())
                        .as("Query result set should have at least one row")
                        .isTrue();

                int resultValue = resultSet.getInt(1);
                assertThat(resultValue)
                        .as("SELECT 1 query result should equal 1")
                        .isEqualTo(1);

                String catalog = connection.getCatalog();
                String driverName = connection.getMetaData().getDriverName();
                String databaseProductName = connection.getMetaData().getDatabaseProductName();

                System.out.println("=================================================");
                System.out.println("DATABASE CONNECTION TEST SUCCESSFUL!");
                System.out.println("Database Product Name : " + databaseProductName);
                System.out.println("Database Driver Name  : " + driverName);
                System.out.println("Connected Database    : " + catalog);
                System.out.println("Query Execution Test  : SELECT 1 => " + resultValue);
                System.out.println("=================================================");
            }
        } catch (Exception e) {
            fail("Failed to connect to the MySQL database or execute verification query: " + e.getMessage(), e);
        }
    }
}
