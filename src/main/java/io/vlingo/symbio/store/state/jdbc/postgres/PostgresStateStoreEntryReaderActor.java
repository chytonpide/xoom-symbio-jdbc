// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state.jdbc.postgres;

import java.util.List;

import io.vlingo.actors.Actor;
import io.vlingo.common.Completes;
import io.vlingo.symbio.Entry;
import io.vlingo.symbio.store.EntryReader;
import io.vlingo.symbio.store.state.StateStoreEntryReader;

public class PostgresStateStoreEntryReaderActor<T extends Entry<?>> extends Actor implements StateStoreEntryReader<T> {
  private final EntryReader.Advice advice;
  private final String name;

  public PostgresStateStoreEntryReaderActor(final EntryReader.Advice advice, final String name) {
    this.advice = advice;
    this.name = name;
  }

  @Override
  public Completes<String> name() {
    return completes().with(name);
  }

  @Override
  public Completes<T> readNext() {
    return null;
  }

  @Override
  public Completes<List<T>> readNext(int maximumEntries) {
    return null;
  }

  @Override
  public void rewind() {

  }

  @Override
  public Completes<String> seekTo(String id) {
    return null;
  }
}
