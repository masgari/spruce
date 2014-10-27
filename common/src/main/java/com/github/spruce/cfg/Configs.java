package com.github.spruce.cfg;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author mamad
 * @since 18/09/14.
 */
public final class Configs {
    public static final Logger LOG = LoggerFactory.getLogger(Configs.class);
    public static final String CONFIG_FILE = "config";

    private static Configs instance;
    private final CompositeConfiguration configuration;

    private final SpruceAppParams spruce;

    private Configs() {
        this(System.getProperty(CONFIG_FILE, System.getProperty("user.home") + "/.spruce"));
    }

    protected Configs(String configPath) {
        configuration = new CompositeConfiguration();

        if (new File(configPath).exists()) {
            try {
                PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration(configPath);
                //automatic reload on external change
                propertiesConfiguration.setReloadingStrategy(new FileChangedReloadingStrategy());
                configuration.addConfiguration(propertiesConfiguration);
            } catch (ConfigurationException e) {
                throw new RuntimeException("Error in creating properties configuration", e);
            }
        } else {
            LOG.error("Config file " + configPath + " does not exist.");
        }

        configuration.addConfiguration(new SystemConfiguration());

        spruce = new SpruceAppParams(new ConfigGroupImpl(SpruceAppParams.NAME, configuration));
    }

    public static SpruceAppParams spruce() {
        return get().spruce;
    }

    static Configs get() {
        if (instance == null) {
            instance = new Configs();
        }
        return instance;
    }

}
