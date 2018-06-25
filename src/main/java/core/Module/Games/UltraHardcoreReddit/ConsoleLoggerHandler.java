package network.reborn.core.Module.Games.UltraHardcoreReddit;

import network.reborn.core.RebornCore;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class ConsoleLoggerHandler extends Handler {
    private RebornCore plugin;

    public ConsoleLoggerHandler(RebornCore instance) {
        this.plugin = instance;
    }

    @Override
    public void close() throws SecurityException {
    }

    @Override
    public void flush() {
    }

    @Override
    public void publish(LogRecord record) {
        UltraHardcoreReddit.setValue(record.getMessage());
    }
}