package com.simpsite.simpsiteservers.ratelimit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class RateVariable {
    private String key;

    @EqualsAndHashCode.Exclude
    private double permitsPerSecond;

    @EqualsAndHashCode.Exclude
    private int permits;

    @EqualsAndHashCode.Exclude
    private int period;
}
