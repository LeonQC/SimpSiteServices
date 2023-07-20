package com.simpsite.simpsiteservers.Annotation;

import com.simpsite.simpsiteservers.Utils.LimitMethod;
import com.simpsite.simpsiteservers.Utils.LimitType;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Inherited
public @interface RateLimit {
    String key() default "RateLimit";

    String prefix() default "Annotation";

    LimitMethod limitMethod() default LimitMethod.PERMITS_BUCKET;

    double permitsPerSecond() default 1.0;

    int period() default 1;

    int permits() default 1;

    LimitType limitType() default LimitType.IP;
}
