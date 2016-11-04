package com.ratelut.apiserver.common;

import com.google.common.collect.Range;

import java.time.Instant;

/**
 * Basic replacement for Joda Interval class.
 *
 * @author Boris Pavacic (boris.pavacic@gmail.com)
 */
public class Interval {
    private final Range<Instant> range;

    public Interval(Instant start, Instant end) {
        range = Range.closedOpen(start, end);
    }

    public boolean contains(Instant instant) {
        return range.contains(instant);
    }

    public Instant getStart() {
        return range.lowerEndpoint();
    }

    public Instant getEnd() {
        return range.upperEndpoint();
    }
}
