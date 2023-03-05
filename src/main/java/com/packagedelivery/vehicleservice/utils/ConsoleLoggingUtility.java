package com.packagedelivery.vehicleservice.utils;


import org.springframework.stereotype.Component;

@Component
public class ConsoleLoggingUtility implements LoggingUtility {
    @Override
    public void logGreen(String message) {
        System.out.print("\u001B[32m" + message + "\u001B[0m");
    }

    @Override
    public void logCyan(String message) {
        System.out.print("\u001B[36m" + message + "\u001B[0m");
    }

    @Override
    public void logYellow(String message) {
        System.out.print("\u001B[33m" + message + "\u001B[0m");
    }

    @Override
    public void logNormal(String message) {
        System.out.print(message);
    }

    @Override
    public void logLine() {
        System.out.println();
    }


}

