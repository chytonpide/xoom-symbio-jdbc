// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.symbio.store.state;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import io.vlingo.xoom.actors.testkit.AccessSafely;
import io.vlingo.xoom.symbio.Entry;
import io.vlingo.xoom.symbio.State;
import io.vlingo.xoom.symbio.store.dispatch.ConfirmDispatchedResultInterest;
import io.vlingo.xoom.symbio.store.dispatch.Dispatchable;
import io.vlingo.xoom.symbio.store.dispatch.Dispatcher;
import io.vlingo.xoom.symbio.store.dispatch.DispatcherControl;

public class MockTextDispatcher implements Dispatcher<Dispatchable<Entry<?>, State.TextState>> {
  private AccessSafely access;

  public final ConfirmDispatchedResultInterest confirmDispatchedResultInterest;
  public DispatcherControl control;
  public final Map<String,State<?>> dispatched = new HashMap<>();
  private final ConcurrentLinkedQueue<Entry<?>> dispatchedEntries = new ConcurrentLinkedQueue<>();
  public final AtomicBoolean processDispatch = new AtomicBoolean(true);
  public final AtomicInteger dispatchAttemptCount = new AtomicInteger(0);

  public MockTextDispatcher(final int testUntilHappenings, final ConfirmDispatchedResultInterest confirmDispatchedResultInterest) {
    this.confirmDispatchedResultInterest = confirmDispatchedResultInterest;
    this.access = AccessSafely.afterCompleting(0);
  }

  @Override
  public void controlWith(final DispatcherControl control) {
    this.control = control;
  }

  @Override
  public void dispatch(Dispatchable<Entry<?>, State.TextState> dispatchable) {
    access.writeUsing("dispatchAttemptCount", 1);
    final String id = dispatchable.id();
    access.writeUsing("dispatched", id, new Dispatch<>(dispatchable.typedState(), dispatchable.entries()));
    control.confirmDispatched(id, confirmDispatchedResultInterest);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public AccessSafely afterCompleting(final int times) {
    access = AccessSafely
      .afterCompleting(times)
      .writingWith("dispatched", (String id, Dispatch dispatch) -> { dispatched.put(id, dispatch.state); dispatchedEntries.addAll(dispatch.entries); })

      .writingWith("dispatchAttemptCount", (ignore) -> { dispatchAttemptCount.incrementAndGet(); })

      .readingWith("dispatchedState", (String id) -> dispatched.get(id))
      .readingWith("dispatchedStateCount", () -> dispatched.size())

      .readingWith("dispatchedEntries", () ->  dispatchedEntries)
      .readingWith("dispatchedEntriesCount", () -> dispatchedEntries.size())

//      .writingWith("processDispatch", (Boolean flag) -> processDispatch.set(flag))
//      .readingWith("processDispatch", () -> processDispatch.get())

      .readingWith("dispatchAttemptCount", () -> dispatchAttemptCount.get())

      .readingWith("dispatched", () -> dispatched);

    return access;
  }

  private static class Dispatch<S extends State<?>,E extends Entry<?>> {
    final Collection<E> entries;
    final S state;

    Dispatch(final S state, final Collection<E> entries) {
      this.state = state;
      this.entries = entries;
    }
  }
}
