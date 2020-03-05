package com.ykc.util.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.mortbay.util.ajax.JSON;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @Decription：接口日志
 * @Author： huang_guoqiang
 * @Date 2019年01月16日 17:38
 * @Version 1.0
 */
@Aspect
@Component
@Order(-1)
@Slf4j
public class LogRecordAspect {
    private ThreadLocal<Long> startTime = new ThreadLocal<>();

    @Pointcut(value = "execution(* com.ykc..controller..*.*(..))")
    public void executeService() {
    }

    @Pointcut(value = "execution(* com.ykc..feign..*.*(..))")
    public void executeFeign() {
    }

    /**
     * 请求信息打印
     *
     * @param joinPoint joinPoint
     */
    @Before(value = "executeService()")
    public void doBefore(JoinPoint joinPoint) {
        startTime.set(System.currentTimeMillis());
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.isNull(attributes)) {
            return;
        }
        HttpServletRequest request = attributes.getRequest();
        log.info("请求URL:{}", request.getRequestURL().toString());
        log.info("请求方式:{}", request.getMethod());
        log.info("请求IP:{}" + request.getRemoteAddr());
        log.info("请求方法:{}", joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        log.info("请求参数:{}", JSON.toString(joinPoint.getArgs()));
    }

    /**
     * Feign请求参数打印
     *
     * @param joinPoint joinPoint
     */
    @Before(value = "executeFeign()")
    public void doBeforeFeign(JoinPoint joinPoint) {
        log.info("Feign请求参数 : {}", JSON.toString(joinPoint.getArgs()));
    }


    /**
     * 接口耗时
     */
    @AfterReturning("executeService()")
    public void doAfterReturning() {
        log.info("接口耗时（毫秒） : " + (System.currentTimeMillis() - startTime.get()));
        startTime.remove();
    }
}
