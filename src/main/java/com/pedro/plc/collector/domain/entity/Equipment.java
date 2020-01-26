package com.pedro.plc.collector.domain.entity;

import com.pedro.plc.collector.domain.valueobject.EquipmentId;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
@Getter
@Setter
public class Equipment {
    private EquipmentId equipmentId;
    private List<Variable> variables;

    public Equipment() {
        this.variables = new ArrayList<>();
    }

    public Variable getFirstVariable() {
        return variables.get(0);
    }

    public Variable getLastVariable() {
        return variables.get(variables.size()-1);
    }
}
