package me.mightyknight.sd.mc215;

import me.mightyknight.sd.mc215.impl.RegistryHandlerImpl;
import me.mightyknight.sd.versioned.RegistryHandler;
import me.mightyknight.sd.versioned.VersionedEntryPoint;

public class SDEntryPoint implements VersionedEntryPoint {

    @Override
    public RegistryHandler getRegistry() {
        return new RegistryHandlerImpl();
    }

}
