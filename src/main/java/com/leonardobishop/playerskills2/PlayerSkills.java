package com.leonardobishop.playerskills2;

import com.google.common.io.ByteStreams;
import com.leonardobishop.playerskills2.commands.SkillsCommand;
import com.leonardobishop.playerskills2.commands.SkillsadminCommand;
import com.leonardobishop.playerskills2.events.ChatEvent;
import com.leonardobishop.playerskills2.events.JoinEvent;
import com.leonardobishop.playerskills2.events.LeaveEvent;
import com.leonardobishop.playerskills2.fundingsource.FundingSource;
import com.leonardobishop.playerskills2.fundingsource.VaultFundingSource;
import com.leonardobishop.playerskills2.fundingsource.XPFundingSource;
import com.leonardobishop.playerskills2.menu.MenuController;
import com.leonardobishop.playerskills2.player.SPlayer;
import com.leonardobishop.playerskills2.skills.*;
import com.leonardobishop.playerskills2.utils.Config;
import com.leonardobishop.playerskills2.utils.ConfigEditWrapper;
import com.leonardobishop.playerskills2.utils.CreatorConfigValue;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;

public class PlayerSkills extends JavaPlugin {

    @Deprecated
    private static PlayerSkills instance;
    private HashMap<String, Skill> skillRegistrar = new HashMap<>();
    private ChatEvent chatEvent;
    private FundingSource fundingSource;
    private boolean verboseLogging;
    private DecimalFormat percentageFormat = new DecimalFormat("#.#");
//    public ProtocolManager protocolManager;


    /**
     * For API access in terms of registering custom skills only.
     * For players, use the methods in class SPlayer.
     *
     * @return the main class
     * @deprecated should really get instance of main class through Bukkit's getPlugin() method
     */
    @Deprecated
    public static PlayerSkills getInstance() {
        return instance;
    }

    public FundingSource getFundingSource() {
        return fundingSource;
    }

    @Override
    public void onEnable() {
        instance = this;
        super.getLogger().info("Thank you for purchasing PlayerSkills2.");
        super.getLogger().info("If this is a leaked version, then shame on you :(");

//        protocolManager = ProtocolLibrary.getProtocolManager();

        percentageFormat.setRoundingMode(RoundingMode.CEILING);

        createConfig();

        GluttonySkill gluttonySkill = new GluttonySkill(this);
        StrengthSkill strengthSkill = new StrengthSkill(this);
        ResistanceSkill resistanceSkill = new ResistanceSkill(this);
        DodgeSkill dodgeSkill = new DodgeSkill(this);
        HealthSkill healthSkill = new HealthSkill(this);
        CriticalsSkill criticalsSkill = new CriticalsSkill(this);
        ArcherySkill archerySkill = new ArcherySkill(this);
        LacerateSkill lacerateSkill = new LacerateSkill(this);
//        MiningSkill miningSkill = new MiningSkill(this);

        registerSkill(gluttonySkill);
        registerSkill(strengthSkill);
        registerSkill(resistanceSkill);
        registerSkill(dodgeSkill);
        registerSkill(healthSkill);
        registerSkill(criticalsSkill);
        registerSkill(archerySkill);
        registerSkill(lacerateSkill);
//        registerSkill(miningSkill);

        this.chatEvent = new ChatEvent(this);
        Bukkit.getPluginCommand("skills").setExecutor(new SkillsCommand(this));
        Bukkit.getPluginCommand("skillsadmin").setExecutor(new SkillsadminCommand(this));
        Bukkit.getPluginManager().registerEvents(new MenuController(), this);
        Bukkit.getPluginManager().registerEvents(new JoinEvent(this), this);
        Bukkit.getPluginManager().registerEvents(new LeaveEvent(this), this);
        Bukkit.getPluginManager().registerEvents(chatEvent, this);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!SPlayer.getPlayers().containsKey(player.getUniqueId())) {
                SPlayer.load(this, player.getUniqueId());
            }
        }

        verboseLogging = Config.get(this, "options.logging.verbose", false).getBoolean();
        if (verboseLogging) {
            logInfo("Verbose logging is enabled. If there is too much spam in the console from PlayerSkills2, you can disable this in the config.");
        }

        Config.ConfigObject fundingSource = Config.get(this, "points.funding-source");
        if (!fundingSource.isNull()) {
            if (fundingSource.getString().equalsIgnoreCase("VAULT")) {
                logInfo("Initialised with Vault as the skill point funding source.");
                this.fundingSource = new VaultFundingSource(this);
            } else {
                logInfo("Initialised with the players XP as the skill point funding source.");
                this.fundingSource = new XPFundingSource();
            }
        } else {
            this.fundingSource = new XPFundingSource();
        }
    }

    @Override
    public void onDisable() {
        for (SPlayer player : SPlayer.getPlayers().values()) {
            SPlayer.save(this, player);
        }
        skillRegistrar.clear();
    }

    public DecimalFormat getPercentageFormat() {
        return percentageFormat;
    }

    public boolean registerSkill(Skill skill) {
        if (super.getConfig().contains("disabled-skills") &&
                super.getConfig().getStringList("disabled-skills").contains(skill.getConfigName())) {
            return false;
        }
        skillRegistrar.put(skill.getConfigName(), skill);
        for (String key : super.getConfig().getConfigurationSection("skills." + skill.getConfigName() + ".config").getKeys(false)) {
            Object value = super.getConfig().get("skills." + skill.getConfigName() + ".config." + key);
            skill.getConfig().put(key, value);
            for (CreatorConfigValue conf : skill.getCreatorConfigValues()) {
                if (conf.getKey().equals(key)) {
                    conf.setValue(value);
                }
            }
        }
        if (super.getConfig().contains("skills." + skill.getConfigName() + ".price-override")) {
            for (String key : super.getConfig().getConfigurationSection("skills." + skill.getConfigName() + ".price-override").getKeys(false)) {
                int price = super.getConfig().getInt("skills." + skill.getConfigName() + ".price-override." + key);
                skill.getPointPriceOverrides().put(Integer.valueOf(key), price);
            }
        }
        skill.setItemLocation("skills." + skill.getConfigName() + ".display");
        skill.enable(this);
        Bukkit.getPluginManager().registerEvents(skill, this);
        return true;
    }

    public void writeSkillConfigCreatorValuesToFile() {
        for (Skill skill : skillRegistrar.values()) {
            for (CreatorConfigValue conf : skill.getCreatorConfigValues()) {
                super.getConfig().set("skills." + skill.getConfigName() + ".config." + conf.getKey(), conf.getValue());
            }
        }
        super.saveConfig();
    }

    public void reloadSkillConfigs() {
        for (Skill skill : skillRegistrar.values()) {
            skill.getConfig().clear();
            for (String key : super.getConfig().getConfigurationSection("skills." + skill.getConfigName() + ".config").getKeys(false)) {
                Object value = super.getConfig().get("skills." + skill.getConfigName() + ".config." + key);
                skill.getConfig().put(key, value);
                for (CreatorConfigValue conf : skill.getCreatorConfigValues()) {
                    if (conf.getKey().equals(key)) {
                        conf.setValue(value);
                    }
                }
            }
            if (super.getConfig().contains("skills." + skill.getConfigName() + ".price-override")) {
                for (String key : super.getConfig().getConfigurationSection("skills." + skill.getConfigName() + ".price-override").getKeys(false)) {
                    int price = super.getConfig().getInt("skills." + skill.getConfigName() + ".price-override." + key);
                    skill.getPointPriceOverrides().put(Integer.valueOf(key), price);
                }
            }
        }
        super.saveConfig();
    }

    public boolean isVerboseLogging() {
        return verboseLogging;
    }

    public HashMap<String, Skill> getSkillRegistrar() {
        return skillRegistrar;
    }

    public void logInfo(String message) {
        super.getLogger().info(message);
    }

    public void logError(String message) {
        super.getLogger().severe(message);
    }

    public void lockPlayerEditor(Player player, ConfigEditWrapper wrapper) {
        chatEvent.getCreatorConfigValue().put(player, wrapper);
    }

    private void createConfig() {
        File directory = new File(String.valueOf(this.getDataFolder()));
        if (!directory.exists() && !directory.isDirectory()) {
            directory.mkdir();
        }

        File config = new File(this.getDataFolder() + File.separator + "config.yml");
        if (!config.exists()) {
            try {
                config.createNewFile();
                try (InputStream in = this.getResource("config.yml")) {
                    OutputStream out = new FileOutputStream(config);
                    byte[] buffer = new byte[1024];
                    int length = in.read(buffer);
                    while (length != -1) {
                        out.write(buffer, 0, length);
                        length = in.read(buffer);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        } else {
//            TODO: finish
//            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(config);
//            File packed = new File(PlayerSkills.class.getClassLoader().getResource("config.yml").getFile());
//            YamlConfiguration packedFile = YamlConfiguration.loadConfiguration(packed);
//
//            boolean changed = false;
//            for (String s : packedFile.getConfigurationSection("skills").getKeys(false)) {
//                if (!yaml.contains("skills." + s)) {
//                    logInfo("Writing new skill to config: " + s);
//                    changed = true;
//                    yaml.set("skills." + s, packedFile.getConfigurationSection("skills." + s));
//                }
//            }
//
//            if (changed) {
//                try {
//                    yaml.save(config);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
        }
        File defaultConfig = new File(this.getDataFolder() + File.separator + "defaultconfig.yml");
        if (defaultConfig.exists()) {
            defaultConfig.delete();
        }
        try {
            defaultConfig.createNewFile();
            try (InputStream in = this.getResource("config.yml")) {
                OutputStream out = new FileOutputStream(defaultConfig);
                byte[] buffer = new byte[1024];
                int length = in.read(buffer);
                while (length != -1) {
                    out.write(buffer, 0, length);
                    length = in.read(buffer);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        File readme = new File(this.getDataFolder() + File.separator + "readme.txt");
        if (readme.exists()) {
            readme.delete();
        }
        try {
            readme.createNewFile();
            try (InputStream in = this.getResource("readme.txt")) {
                OutputStream out = new FileOutputStream(readme);
                byte[] buffer = new byte[1024];
                int length = in.read(buffer);
                while (length != -1) {
                    out.write(buffer, 0, length);
                    length = in.read(buffer);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
