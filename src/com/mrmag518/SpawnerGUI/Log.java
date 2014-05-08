package com.mrmag518.spawnergui;

import java.util.logging.Logger;

public class Log {
    private static final Logger log = Logger.getLogger("Minecraft");
    private static final String PREFIX = "[SpawnerGUI] ";
    
    public static void info(String output) {
        log.info(PREFIX + output);
    }
    
    public static void severe(String output) {
        log.severe(PREFIX + output);
    }
    
    public static void warning(String output) {
        log.warning(PREFIX + output);
    }
}
