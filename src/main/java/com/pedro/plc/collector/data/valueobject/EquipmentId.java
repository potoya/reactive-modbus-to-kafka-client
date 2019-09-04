package com.pedro.plc.collector.data.valueobject;

import lombok.ToString;

import java.util.Objects;

public class EquipmentId {
    private String id;

    public static EquipmentId fromString(String id) {
        return new EquipmentId(id);
    }

    private EquipmentId(String id) {
        Objects.requireNonNull(id);
        this.id = id.replaceAll(" ", "_").toUpperCase();
    }

    public Boolean isNotEmpty() {
        return !this.id.isEmpty();
    }

    @Override
    public boolean equals(Object obj) {
        final EquipmentId other = (EquipmentId) obj;
        return id.equals(String.valueOf(other.id));
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return id;
    }
}
