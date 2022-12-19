package me.mightyknight.sd.versioned;

import net.fabricmc.loader.api.FabricLoader;

import java.util.List;

public class Versioned {

    public static RegistryHandler REGISTRY;

    public static void load() {
        List<VersionedEntryPoint> entryPoints = FabricLoader.getInstance().
                getEntrypoints("shield-disruptor-versioned", VersionedEntryPoint.class);
        if(entryPoints.size() != 1) {
            // Either none or too many version specific entry points were loaded
            // Crash controlled and send feedback
            throw new RuntimeException("Shield Disruptor didn't load correctly! You are propably using an unsupported version of Minecraft");
        }
        VersionedEntryPoint entryPoint = FabricLoader.getInstance()
                .getEntrypoints("shield-disruptor-versioned", VersionedEntryPoint.class).get(0);
        if (entryPoint == null) {
            throw new RuntimeException("Could not find versioned entrypoint, are you using an unsupported version of Minecraft?");
        }

        REGISTRY = entryPoint.getRegistry();
    }
}
