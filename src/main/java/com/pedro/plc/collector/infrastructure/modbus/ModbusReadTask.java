package com.pedro.plc.collector.infrastructure.modbus;

import com.digitalpetri.modbus.master.ModbusTcpMaster;
import com.digitalpetri.modbus.requests.ReadHoldingRegistersRequest;
import com.digitalpetri.modbus.responses.ReadHoldingRegistersResponse;
import com.pedro.plc.collector.domain.entity.ModbusDevice;
import com.pedro.plc.collector.domain.valueobject.EquipmentHoldingRegisters;
import com.pedro.plc.collector.domain.valueobject.EquipmentRawMeasurements;
import com.pedro.plc.collector.gateway.DataGateway;
import com.pedro.plc.collector.services.EquipmentService;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Value
public class ModbusReadTask implements Runnable, Closeable {
    private ModbusTcpMaster modbusMaster;
    private DataGateway dataGateway;
    private EquipmentService equipmentService;

    public ModbusReadTask(ModbusDevice modbusDevice, DataGateway dataGateway) {
        this.modbusMaster = new ModbusTcpMaster(modbusDevice.getConnection().getModbusTcpMasterConfig());
        this.dataGateway = dataGateway;
        this.equipmentService = EquipmentService.getInstance();
    }

    public void tryConnect(int retries) throws Exception {
        Boolean connected = false;
        while (!connected && retries-- > 0) {
            try {
                this.modbusMaster.connect().get(10, TimeUnit.SECONDS);
                connected = true;
            } catch (Exception e) {
                log.error("Could not connect to {}:{} - {} retries left",
                        this.modbusMaster.getConfig().getAddress(),
                        this.modbusMaster.getConfig().getPort(),
                        retries,
                        e);
            }
        }

        if (!connected) throw new Exception("Connection was not possible after atempting retries");
    }

    @Override
    public void run() {
        try {
            List<EquipmentHoldingRegisters> allEquipmentHoldingRegisters = equipmentService.findAllEquipmentHoldingRegisters();
            for (EquipmentHoldingRegisters equipmentHoldingRegisters : allEquipmentHoldingRegisters) {
                String equipmentId = equipmentHoldingRegisters.getEquipmentId();
                int address = equipmentHoldingRegisters.getStartRegisterIndex();
                int quantity = equipmentHoldingRegisters.getNumRegisters();
                ReadHoldingRegistersRequest request = new ReadHoldingRegistersRequest(address, quantity);

                CompletableFuture<ReadHoldingRegistersResponse> future = modbusMaster.sendRequest(request, ModbusDevice.UNIT_ID);
                log.info("Requested equipment [{}] variables from PLC", equipmentId);
                handleResponse(getResponse(future), equipmentId, address, quantity);
            }
        } catch (Exception e) {
            log.error("error at request timeee", e);
        }
    }

    private ReadHoldingRegistersResponse getResponse(CompletableFuture<ReadHoldingRegistersResponse> future) {
        try {
            ReadHoldingRegistersResponse response = future.get(1000, TimeUnit.MILLISECONDS);
            return response;
        } catch (Exception e) {
            throw new IllegalStateException("Could not get a response");
        }
    }

    private void handleResponse(ReadHoldingRegistersResponse response, String equipmentId, int address, int quantity) {
        try {
            log.info("Recieved equipment [{}] variables from PLC", equipmentId);
            ByteBuf readedRegistersBytes = response.getRegisters();
            byte[] byteArray = new byte[readedRegistersBytes.readableBytes()];

            readedRegistersBytes.readBytes(byteArray);

            ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
            byteBuffer.order(ByteOrder.BIG_ENDIAN);

            EquipmentRawMeasurements rawEquipmentMeasurment = new EquipmentRawMeasurements(equipmentId, byteBuffer);
            dataGateway.send(rawEquipmentMeasurment);
        } catch (Exception e) {
            log.error("Exception while handling response", e);
        } finally {
            ReferenceCountUtil.release(response);
        }

    }

    @Override
    public void close() throws IOException {
        this.modbusMaster.disconnect();
    }
}
