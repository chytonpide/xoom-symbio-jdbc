package io.vlingo.xoom.symbio.store.journal.jdbc;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.vlingo.xoom.actors.Definition;
import io.vlingo.xoom.symbio.State.TextState;
import io.vlingo.xoom.symbio.store.common.event.TestEvent;
import io.vlingo.xoom.symbio.store.journal.EntityStream;
import io.vlingo.xoom.symbio.store.journal.StreamReader;
import io.vlingo.xoom.symbio.store.journal.jdbc.JDBCStreamReaderActor.JDBCStreamReaderInstantiator;

public abstract class JDBCStreamReaderActorTest extends BaseJournalTest {
    private StreamReader<String> eventStreamReader;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        eventStreamReader = world.actorFor(
                StreamReader.class,
                Definition.has(JDBCStreamReaderActor.class,
                        new JDBCStreamReaderInstantiator(configuration))
        );

        assert insertEvent(1) == 1;
        assert insertEvent(2) == 2;
        assert insertEvent(3) == 3;
        assert insertEvent(4) == 4;
    }

    @Test
    public void testThatCanReadAllEventsFromJournal() throws Exception {
        EntityStream<String> stream = eventStreamReader.streamFor(streamName).await();
        assertEquals(TextState.Null, stream.snapshot);
        assertEquals(4, stream.streamVersion);
        assertEquals(stream.streamName, streamName);

        AtomicInteger eventNumber = new AtomicInteger(1);
        stream.entries.forEach(event -> assertEquals(eventNumber.getAndIncrement(), parse(event).number));
    }

    @Test
    public void testThatCanReadAllEventsFromJournalBasedOnOffsetReturnSnapshot() throws Exception {
        TestEvent snapshotState = new TestEvent(streamName, 2);
        insertSnapshot(2, snapshotState);

        EntityStream<String> stream = eventStreamReader.streamFor(streamName, 1).await();
        assertEquals(2, stream.snapshot.dataVersion);
        assertEquals(snapshotState, gson.fromJson(stream.snapshot.data, TestEvent.class));
        assertEquals(4, stream.streamVersion);
        assertEquals(stream.streamName, streamName);

        Assert.assertEquals(3, stream.entries.size());
        Assert.assertEquals(2, parse(stream.entries.get(0)).number);
        Assert.assertEquals(3, parse(stream.entries.get(1)).number);
        Assert.assertEquals(4, parse(stream.entries.get(2)).number);
    }

    @Test
    public void testThatCanReadAllEventsFromJournalBasedOnOffsetDoesNotReturnSnapshotIfOffsetIsHigher() throws Exception {
        TestEvent snapshotState = new TestEvent(streamName, 1);
        insertSnapshot(1, snapshotState);

        EntityStream<String> stream = eventStreamReader.streamFor(streamName, 4).await();

        assertEquals(TextState.Null, stream.snapshot);
        assertEquals(streamName, stream.streamName);
        assertEquals(4, stream.streamVersion);

        Assert.assertEquals(1, stream.entries.size());
        Assert.assertEquals(4, parse(stream.entries.get(0)).number);
    }
}

