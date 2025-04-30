package com.github.Debris.DebrisClient;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.io.File;

public class DebrisClient {
    public static final String MOD_ID = "debris_client";
    public static final String MOD_NAME = "DebrisClient";
    public static final String MOD_VERSION = "1.0.0";

    public static final String CONFIG_DIR = "./config" + File.separator + MOD_NAME + File.separator;

    public static final Logger logger = LogUtils.getLogger();
}
