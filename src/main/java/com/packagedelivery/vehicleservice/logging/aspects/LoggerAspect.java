package com.packagedelivery.vehicleservice.logging.aspects;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.packagedelivery.vehicleservice.utils.LoggingUtility;
import com.packagedelivery.vehicleservice.logging.annotations.DebugLog;
import com.packagedelivery.vehicleservice.logging.annotations.InfoLog;
import com.packagedelivery.vehicleservice.datamodels.enums.LogLevels;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Aspect
@Component
public class LoggerAspect {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:ms");

    private final LoggingUtility loggingUtility;

     final ObjectMapper objectMapper;

    @Value("${spring.logging.level}")
    private String logLevel;
    public LoggerAspect(LoggingUtility loggingUtility, ObjectMapper objectMapper) {
        this.loggingUtility = loggingUtility;
        this.objectMapper = objectMapper;
    }

    @Around("@annotation(com.packagedelivery.vehicleservice.logging.annotations.InfoLog)")
    public Object logInfo(ProceedingJoinPoint joinPoint) throws Throwable {
        if (LogLevels.valueOf(logLevel).equals(LogLevels.INFO) || LogLevels.valueOf(logLevel).equals(LogLevels.DEBUG)) {
            if ((((MethodSignature) joinPoint.getSignature()).getMethod()).isAnnotationPresent(InfoLog.class)) {
                InfoLog infoLog = (((MethodSignature) joinPoint.getSignature()).getMethod()).getAnnotation(InfoLog.class);
                String logTime = LocalDateTime.now().format(formatter);
                this.loggingUtility.logNormal(logTime + " ");
                this.loggingUtility.logGreen(LogLevels.INFO.name() + " ");
                this.loggingUtility.logCyan(infoLog.value());
                this.loggingUtility.logLine();
            }
        }

        Object result = joinPoint.proceed();

        return result;
    }

    @Around("@annotation(com.packagedelivery.vehicleservice.logging.annotations.DebugLog)")
    public Object logDebug(ProceedingJoinPoint joinPoint) throws Throwable {
        DebugLog debugLog = null;

        if (LogLevels.valueOf(logLevel).equals(LogLevels.DEBUG)) {
            if ((((MethodSignature) joinPoint.getSignature()).getMethod()).isAnnotationPresent(DebugLog.class)) {
                debugLog = (((MethodSignature) joinPoint.getSignature()).getMethod()).getAnnotation(DebugLog.class);
            }
        }

        if (debugLog != null) {
            try {
                String logTime = LocalDateTime.now().format(formatter);
                this.loggingUtility.logNormal(logTime + " ");
                this.loggingUtility.logYellow(LogLevels.DEBUG.name() + " ");
                this.loggingUtility.logYellow("Request JSON - " + "to be continued");
                this.loggingUtility.logLine();
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }

        if (debugLog != null) {
            String logTime = LocalDateTime.now().format(formatter);
            this.loggingUtility.logNormal(logTime + " ");
            this.loggingUtility.logYellow(LogLevels.DEBUG.name() + " ");
            this.loggingUtility.logYellow(debugLog.value() + " Starting execution at " + logTime);
            this.loggingUtility.logLine();
        }

        Object result = joinPoint.proceed();

        if (debugLog != null) {
            String logTime = LocalDateTime.now().format(formatter);
            this.loggingUtility.logNormal(logTime + " ");
            this.loggingUtility.logYellow(LogLevels.DEBUG.name() + " ");
            this.loggingUtility.logYellow(debugLog.value() + " Finished execution at " + logTime);
            this.loggingUtility.logLine();
        }

        return result;
    }
}
