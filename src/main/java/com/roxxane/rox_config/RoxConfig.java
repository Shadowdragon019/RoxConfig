package com.roxxane.rox_config;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@SuppressWarnings("unused")
@Mod(RoxConfig.id)
public class RoxConfig {
    public static final String id = "rox_config";
    public static final String displayName = "Rox Config";
    public static final Logger logger = LogUtils.getLogger();
    public static final Config config = new TestConfig();
}