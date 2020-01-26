package com.pedro.plc.collector.infrastructure.modbus;

import com.pedro.plc.collector.domain.entity.ModbusDevice;
import com.pedro.plc.collector.gateway.DataGateway;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class ModbusConnector extends Thread {

    private static final int POLL_PERIOD = 1_000;

    private ScheduledExecutorService readerExecutorService;
    private ModbusDevice connectedModbusDevice;

    public ModbusConnector(ModbusDevice deviceToConnect) {
        readerExecutorService = Executors.newSingleThreadScheduledExecutor();
        connectedModbusDevice = deviceToConnect;
    }

    @Override
    public void run() {
        DataGateway dq = new DataGateway();
        try (ModbusReadTask modbusReadTask = new ModbusReadTask(connectedModbusDevice, dq)) {
            modbusReadTask.tryConnect(1);
            ScheduledFuture<?> scheduledFuture =
                    readerExecutorService.scheduleAtFixedRate(modbusReadTask, 100, POLL_PERIOD, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("Failed initialization of thread contexts", e);
        }

        while(true) {
            try {
                Thread.sleep(POLL_PERIOD*10L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Keep this object live somehow so I can suspend scheduled thread.
    }

}
