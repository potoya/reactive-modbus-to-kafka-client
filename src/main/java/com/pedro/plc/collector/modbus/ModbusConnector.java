package com.pedro.plc.collector.modbus;

import com.pedro.plc.collector.gateway.DataGateway;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class ModbusConnector extends Thread {

    private static final int POLL_PERIOD = 10_000;

    private ScheduledExecutorService readerExecutorService;

    public ModbusConnector() {
        readerExecutorService = Executors.newSingleThreadScheduledExecutor();
    }

    private void bootstrap() {
        DataGateway dq = new DataGateway();
        try (ModbusReadTask modbusReadTask = new ModbusReadTask(dq)) {
            modbusReadTask.tryConnect(1);
            //TODO control reconnection logic by sleeping...
            readerExecutorService.scheduleAtFixedRate(modbusReadTask, 100, POLL_PERIOD, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("Failed initialization of thread contexts", e);
        }
    }


    @Override
    public void run() {
        bootstrap();
        // Keep this object live somehow so I can suspend scheduled thread.
    }

}
