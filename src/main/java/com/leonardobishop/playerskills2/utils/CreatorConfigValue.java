package com.leonardobishop.playerskills2.utils;

/**
 * Used to generate default configurations and for the in game editor.
 */
public class CreatorConfigValue {

    private ConfigType type;
    private String key;
    private Object value;
    private Object def;
    private String description;
    private boolean required;

    public CreatorConfigValue(ConfigType type, String key, Object value, Object def, String description, boolean required) {
        this.type = type;
        this.key = key;
        this.value = value;
        this.def = def;
        this.description = description;
        this.required = required;
    }

    public CreatorConfigValue(ConfigType type, String key, Object value, String description, boolean required) {
        this.type = type;
        this.key = key;
        this.value = value;
        this.def = value;
        this.description = description;
        this.required = required;
    }

    public ConfigType getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getDefault() {
        return def;
    }

    public String getDescription() {
        return description;
    }

    public boolean getRequired() {
        return required;
    }


}
