package com.leonardobishop.playerskills2.utils;

public enum ConfigType {

    BOOLEAN("Boolean", "Either 'true' or 'false'"),
    NUMBER("Number", "Any number (can be a decimal)"),
    DOUBLE("Double", "Any decimal value"),
    INTEGER("Integer", "Any integer (no decimal values)"),
    LIST("List of type <{type}>", "{help} in the format '[element1, element2, ...]'"),
    STRING("String", "Any series of characters");

    private String description;
    private String help;

    ConfigType(String description, String help) {
        this.description = description;
        this.help = help;
    }

    public String getDescription() {
        //TODO expand lol
        return description.replace("{type}", STRING.description);
    }

    public String getHelp() {
        return help.replace("{help}", STRING.help);
    }
}
