package com.levelout.web.enums;

public enum IfcSchema {
    ifc2x3tc1("Ifc2x3tc1"),
    ifc4("Ifc4");

    String deserializerName;

    private IfcSchema(String deserializerName) {
        this.deserializerName = deserializerName;
    }

    public String getDeserializerName() {
        return this.deserializerName;
    }
}
