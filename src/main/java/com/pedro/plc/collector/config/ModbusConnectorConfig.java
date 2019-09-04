package com.pedro.plc.collector.config;

import com.digitalpetri.modbus.master.ModbusTcpMasterConfig;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModbusConnectorConfig {
    private static final String HOST = "172.25.19.6";
    private static final int PORT = 502;
    public static final int UNIT_ID = 1;
    public static ModbusConnectorConfig instance = new ModbusConnectorConfig();

    private ModbusTcpMasterConfig modbusTcpMasterConfig;

    public static ModbusConnectorConfig getInstance() { return instance; }

    private ModbusConnectorConfig() {
        this.modbusTcpMasterConfig = buildConfig(HOST, PORT);
    }

    public ModbusTcpMasterConfig buildModbusTcpMasterConfig(String host, Integer port) {
        return buildConfig(host, port);
    }

    private static ModbusTcpMasterConfig buildConfig(String host, Integer port) {
        return new ModbusTcpMasterConfig.Builder(host)
                .setPort(port)
                .build();
    }
}
