package me.mightyknight.sd.common;

import me.mightyknight.sd.versioned.Versioned;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class PreLaunch implements PreLaunchEntrypoint {

    @Override
    public void onPreLaunch() {
        if (FabricLoader.getInstance().getAllMods().stream().noneMatch(container -> container
                .getMetadata()
                .getId().startsWith("shield-disruptor-mc"))) {
            throw new RuntimeException("Shield Disruptor didn't load correctly! You're probably using an unsupported version of Minecraft.");
        }

        Versioned.load();
    }
}
