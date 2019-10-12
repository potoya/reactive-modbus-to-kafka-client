/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pedro.modbus.collector;

import com.digitalpetri.modbus.master.ModbusTcpMaster;
import com.digitalpetri.modbus.master.ModbusTcpMasterConfig;

/**
 *
 * @author PC
 */
public class ModbusTcpTestMasterProvider {

    public static ModbusTcpMaster get() {
        ModbusTcpMasterConfig config
                = new ModbusTcpMasterConfig.Builder("localhost")
                .setPort(50601)
                .build();
        return new ModbusTcpMaster(config);
    }
    
}
