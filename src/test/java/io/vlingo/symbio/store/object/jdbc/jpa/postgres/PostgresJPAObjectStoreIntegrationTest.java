// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.object.jdbc.jpa.postgres;

import io.vlingo.actors.Logger;
import io.vlingo.symbio.StateAdapterProvider;
import io.vlingo.symbio.store.DataFormat;
import io.vlingo.symbio.store.common.jdbc.Configuration;
import io.vlingo.symbio.store.common.jdbc.ConnectionProvider;
import io.vlingo.symbio.store.common.jdbc.postgres.PostgresConfigurationProvider;
import io.vlingo.symbio.store.object.jdbc.JDBCObjectStoreEntryJournalQueries;
import io.vlingo.symbio.store.object.jdbc.PostgresObjectStoreEntryJournalQueries;
import io.vlingo.symbio.store.object.jdbc.jpa.JDBCObjectStoreEntryReaderTest;
import io.vlingo.symbio.store.object.jdbc.jpa.JPAObjectStoreDelegate;
import io.vlingo.symbio.store.testcontainers.SharedPostgreSQLContainer;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class PostgresJPAObjectStoreIntegrationTest extends JDBCObjectStoreEntryReaderTest {
    private SharedPostgreSQLContainer postgresContainer = SharedPostgreSQLContainer.getInstance();

    @Override
    protected Configuration createAdminConfiguration() throws Exception {
        return postgresContainer.testConfiguration(DataFormat.Text);
    }

    @Override
    protected JPAObjectStoreDelegate createDelegate(Map<String, Object> properties, String originatorId,
                                                    StateAdapterProvider stateAdapterProvider, Logger logger) {
        return new JPAObjectStoreDelegate(JPAObjectStoreDelegate.JPA_POSTGRES_PERSISTENCE_UNIT, properties, "TEST",
                stateAdapterProvider, logger);
    }

    @Override
    protected ConnectionProvider createConnectionProvider() {
        return new ConnectionProvider(
                "org.postgresql.Driver",
                "jdbc:postgresql://" + postgresContainer.getHost() + ":" + postgresContainer.getMappedPort(SharedPostgreSQLContainer.POSTGRESQL_PORT) + "/",
                testDatabaseName,
                postgresContainer.getUsername(),
                postgresContainer.getPassword(),
                false);
    }

    @Override
    protected JDBCObjectStoreEntryJournalQueries createQueries(Connection connection) {
        return new PostgresObjectStoreEntryJournalQueries(connection);
    }

    @Override
    protected void createTestDatabase() throws Exception {
        PostgresConfigurationProvider.interest.createDatabase(adminConfiguration.connection, testDatabaseName);
    }

    @Override
    protected void dropTestDatabase() throws Exception {
        PostgresConfigurationProvider.interest.dropDatabase(adminConfiguration.connection, testDatabaseName);
    }

    @Override
    protected Map<String, Object> getDatabaseSpecificProperties(String databaseNamePostfix) {
        Map<String, Object> properties = new HashMap<>();

        properties.put("javax.persistence.jdbc.driver", "org.postgresql.Driver");
        properties.put("javax.persistence.jdbc.url", "jdbc:postgresql://" + postgresContainer.getHost() + ":" + postgresContainer.getMappedPort(SharedPostgreSQLContainer.POSTGRESQL_PORT) + "/" + databaseNamePostfix);
        properties.put("javax.persistence.jdbc.user", postgresContainer.getUsername());
        properties.put("javax.persistence.jdbc.password", postgresContainer.getPassword());

        return properties;
    }
}
