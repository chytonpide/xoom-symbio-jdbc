// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state.jdbc.mysql;

import io.vlingo.actors.Logger;
import io.vlingo.symbio.store.DataFormat;
import io.vlingo.symbio.store.common.jdbc.Configuration;
import io.vlingo.symbio.store.state.StateStore;
import io.vlingo.symbio.store.state.jdbc.JDBCTextStateStoreEntryTest;
import io.vlingo.symbio.store.testcontainers.SharedMySQLContainer;
import org.junit.Ignore;

@Ignore
public class MySQLJDBCTextStateStoreEntryTest extends JDBCTextStateStoreEntryTest {

  private SharedMySQLContainer mysqlContainer = SharedMySQLContainer.getInstance();

  @Override
  protected StateStore.StorageDelegate storageDelegate(Configuration.TestConfiguration configuration, Logger logger) {
    return new MySQLStorageDelegate(configuration, logger);
  }

  @Override
  protected Configuration.TestConfiguration testConfiguration(DataFormat format) throws Exception {
    System.out.println("Starting: MySQLJDBCTextStateStoreEntryActorTest: testConfiguration()");
    return mysqlContainer.testConfiguration(format);
  }
}
