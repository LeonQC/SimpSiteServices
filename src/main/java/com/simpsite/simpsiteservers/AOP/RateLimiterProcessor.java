package com.simpsite.simpsiteservers.AOP;

import com.simpsite.simpsiteservers.Annotation.RateLimit;
import com.simpsite.simpsiteservers.Utils.ClientIPUtils;
import com.simpsite.simpsiteservers.Utils.LimitMethod;
import com.simpsite.simpsiteservers.Utils.LimitType;
import com.simpsite.simpsiteservers.ratelimit.RateLimiter;
import com.simpsite.simpsiteservers.ratelimit.RateLimiterExecutor;
import com.simpsite.simpsiteservers.ratelimit.RateVariable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
@Aspect
@Configuration
public class RateLimiterProcessor {
    private static final Logger log = LoggerFactory.getLogger(RateLimiterProcessor.class);
    private final RateLimiterExecutor rateLimiterExecutor;

    @Autowired
    public RateLimiterProcessor(RateLimiterExecutor rateLimiterExecutor) {
        this.rateLimiterExecutor = rateLimiterExecutor;
    }

    @Around("execution(public * *(..)) && @annotation(com.simpsite.simpsiteservers.Annotation.RateLimit)")
    public Object processor(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        if (method == null) {
            return null;
        } else {
            RateLimit rateLimitAnnotation = method.getAnnotation(RateLimit.class);
            LimitType limitType = rateLimitAnnotation.limitType();
            String key = getLimiterKey(request, method, rateLimitAnnotation, limitType);
            key = StringUtils.join(rateLimitAnnotation.prefix(), key);

            double permitsPerSecond = rateLimitAnnotation.permitsPerSecond();
            int period = rateLimitAnnotation.period();
            int permits = rateLimitAnnotation.permits();
            LimitMethod limitMethod = rateLimitAnnotation.limitMethod();

            RateVariable rateVariables = new RateVariable();
            rateVariables.setKey(key);
            rateVariables.setPermits(permits);
            rateVariables.setPeriod(period);
            rateVariables.setPermitsPerSecond(permitsPerSecond);
            RateLimiter rateLimiter = switch (limitMethod) {
                case PERMITS_BUCKET -> rateLimiterExecutor.getPermitBucketRateLimiter();
            };

            if (rateLimiter.isRateLimited(key, rateVariables)) {
                log.info("Access to {} from {} is rate limited", method.getName(), key);
                sendFallback();
                return null;
            }
        }

        return joinPoint.proceed();
    }

    private String getLimiterKey(HttpServletRequest request, Method method, RateLimit rateLimitAnnotation, LimitType limitType) {
        String key = StringUtils.upperCase(
                method.getDeclaringClass().getSimpleName() + ":" + method.getName()
        );
        return switch (limitType) {
            case IP -> key + "_" + LimitType.IP + ":" + ClientIPUtils.getClientIpAddress(request);
        };
    }

    private void sendFallback() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = requestAttributes.getResponse();
        response.setContentType("text/html;charset=UTF-8");
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        log.info("TOO MANY REQUESTS");
    }
}
