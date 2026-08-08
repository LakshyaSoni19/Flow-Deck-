package com.technomancarai.tms;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

@SpringBootTest
class SchemaInspectionTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void inspectDatabaseSchema() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            String catalog = connection.getCatalog();

            System.out.println("=== CATALOG: " + catalog + " ===");

            try (ResultSet tables = metaData.getTables(catalog, null, "%", new String[]{"TABLE"})) {
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    System.out.println("\nTABLE: " + tableName);
                    System.out.println("----------------------------------------");

                    try (ResultSet columns = metaData.getColumns(catalog, null, tableName, "%")) {
                        while (columns.next()) {
                            String colName = columns.getString("COLUMN_NAME");
                            String typeName = columns.getString("TYPE_NAME");
                            int colSize = columns.getInt("COLUMN_SIZE");
                            String isNullable = columns.getString("IS_NULLABLE");

                            System.out.printf("  COLUMN: %-20s TYPE: %-15s SIZE: %-5d NULLABLE: %s%n",
                                    colName, typeName, colSize, isNullable);
                        }
                    }

                    try (ResultSet primaryKeys = metaData.getPrimaryKeys(catalog, null, tableName)) {
                        while (primaryKeys.next()) {
                            System.out.println("  PRIMARY KEY: " + primaryKeys.getString("COLUMN_NAME"));
                        }
                    }

                    try (ResultSet foreignKeys = metaData.getImportedKeys(catalog, null, tableName)) {
                        while (foreignKeys.next()) {
                            String pkTableName = foreignKeys.getString("PKTABLE_NAME");
                            String pkColumnName = foreignKeys.getString("PKCOLUMN_NAME");
                            String fkColumnName = foreignKeys.getString("FKCOLUMN_NAME");
                            String fkName = foreignKeys.getString("FK_NAME");
                            System.out.printf("  FOREIGN KEY: %s (%s) -> %s (%s) [%s]%n",
                                    fkColumnName, tableName, pkColumnName, pkTableName, fkName);
                        }
                    }
                }
            }
        }
    }
}
