package com.newrelic.app.model;

import lombok.Builder;
import lombok.Data;

/**
 * Class that is used to pass the statistics of the numbers received like unique, duplicate
 */
@Data
@Builder
public class DataCollected {
    private long unique;
    private long duplicate;
    private long totalUnique;
}
