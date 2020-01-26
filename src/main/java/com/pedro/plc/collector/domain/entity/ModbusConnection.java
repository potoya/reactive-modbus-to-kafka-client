package com.pedro.plc.collector.domain.entity;

import com.digitalpetri.modbus.master.ModbusTcpMasterConfig;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModbusConnection {
    private String host;
    private Integer port;
    private ModbusTcpMasterConfig modbusTcpMasterConfig;

    public static ModbusConnection of(String host, Integer port) {
        ModbusConnection self = new ModbusConnection();
        self.host = host;
        self.port = port;
        self.modbusTcpMasterConfig =
                new ModbusTcpMasterConfig.Builder(host)
                        .setPort(port)
                        .build();
        return self;
    }

    private ModbusConnection() {
    }
}
