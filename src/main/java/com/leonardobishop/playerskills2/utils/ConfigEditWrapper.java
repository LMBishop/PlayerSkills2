package com.leonardobishop.playerskills2.utils;

import com.leonardobishop.playerskills2.skills.Skill;

public class ConfigEditWrapper {

    private CreatorConfigValue configValue;
    private Skill skill;

    public ConfigEditWrapper(CreatorConfigValue configValue, Skill skill) {
        this.configValue = configValue;
        this.skill = skill;
    }

    public CreatorConfigValue getConfigValue() {
        return configValue;
    }

    public Skill getSkill() {
        return skill;
    }
}
