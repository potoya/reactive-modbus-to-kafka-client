package com.pedro.plc.collector.domain.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter(AccessLevel.PRIVATE)
public class Variable {

    private String name;

    private String units;

    @Setter(AccessLevel.NONE)
    private Integer modbusAddress;

    public Variable(String name, String units, Integer modbusAddress){
        setName(name);
        setUnits(units);
        setModbusAddress(modbusAddress);
    }

    public Variable(String name, String units, Double modbusAddress){
        this(name, units, modbusAddress.intValue());
    }


    private void setModbusAddress(Integer modbusAddress) {
        if (modbusAddress < 40000) {
            throw new IllegalArgumentException(modbusAddress + " " +
                    "register address should be greater than 40000 for modbus");
        }
        this.modbusAddress = modbusAddress;
    }
}
