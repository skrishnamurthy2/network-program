package com.newrelic.app.service;

import com.newrelic.app.model.DataCollected;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DataCollectorTest {
    @Mock
    private Logger logger;

    @Test
    public void should_add_number() {
        DataCollector collector = new DataCollector(logger);
        collector.collect("123");
        DataCollected data = collector.report();
        Assertions.assertEquals(1, data.getUnique());
        Assertions.assertEquals(0, data.getDuplicate());
        Assertions.assertEquals(1, data.getTotalUnique());
    }

    @Test
    public void should_add_multiple_unique_number() {
        DataCollector collector = new DataCollector(logger);
        collector.collect("123");
        collector.collect("566");
        DataCollected data = collector.report();
        Assertions.assertEquals(2, data.getUnique());
        Assertions.assertEquals(0, data.getDuplicate());
        Assertions.assertEquals(2, data.getTotalUnique());
    }

    @Test
    public void should_not_add_duplicate() {
        DataCollector collector = new DataCollector(logger);
        collector.collect("123");
        collector.collect("123");
        DataCollected data = collector.report();
        Assertions.assertEquals(1, data.getUnique());
        Assertions.assertEquals(1, data.getDuplicate());
        Assertions.assertEquals(1, data.getTotalUnique());
    }

    @Test
    public void should_reset_count_after_report() {
        DataCollector collector = new DataCollector(logger);
        collector.collect("123");
        DataCollected data = collector.report();
        Assertions.assertEquals(1, data.getUnique());
        Assertions.assertEquals(0, data.getDuplicate());
        Assertions.assertEquals(1, data.getTotalUnique());

        collector.collect("123");
        data = collector.report();
        Assertions.assertEquals(0, data.getUnique());
        Assertions.assertEquals(1, data.getDuplicate());
        Assertions.assertEquals(1, data.getTotalUnique());
    }

    @Test
    public void should_error_for_null_string() {
        DataCollector collector = new DataCollector(logger);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
           collector.collect(null);
        });
    }

    @Test
    public void should_error_for_non_number_string() {
        DataCollector collector = new DataCollector(logger);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            collector.collect("abc");
        });
    }
}
