/*
MIT License

Copyright (c) 2019 - 2024 Virtuoel

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package me.mightyknight.sd.multiversion_mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import me.mightyknight.sd.common.ShieldDisruptor;
import me.mightyknight.sd.multiversion_mixin.version.PehkuiVersionUtils;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;

// The reflection code adapted and modified from Pehkui, a big thank you to Virtuoel
public class ReflectionUtils {


    public static final MethodHandle CONSTRUCT_ID_FROM_STRING, CONSTRUCT_ID_FROM_STRINGS;
    final Version version = FabricLoader.getInstance().getModContainer("minecraft").get().getMetadata().getVersion();

    static {

        final Int2ObjectMap<MethodHandle> h = new Int2ObjectArrayMap<>();

        final MethodHandles.Lookup lookup = MethodHandles.lookup();
        String mapped = "unset";

        try {

            final boolean is1206Minus = PehkuiVersionUtils.MINOR < 20 || (PehkuiVersionUtils.MINOR == 20 && PehkuiVersionUtils.PATCH <= 6);;

            // 1.20.6 was using "new Identifier(String)"
            if (is1206Minus) {
                h.put(9, lookup.unreflectConstructor(Identifier.class.getDeclaredConstructor(String.class)));
                h.put(10, lookup.unreflectConstructor(Identifier.class.getDeclaredConstructor(String.class, String.class)));
            }

        } catch (NoSuchMethodException | SecurityException | IllegalAccessException e) {
            ShieldDisruptor.LOGGER.error("Current name lookup: {}", mapped);
            ShieldDisruptor.LOGGER.catching(e);
        }

        CONSTRUCT_ID_FROM_STRING = h.get(9);
        CONSTRUCT_ID_FROM_STRINGS = h.get(10);

    }

    public static Identifier constructIdentifier(final String id) {
        if (CONSTRUCT_ID_FROM_STRING != null) {
            try {
                return (Identifier) CONSTRUCT_ID_FROM_STRING.invoke(id);
            } catch (final InvalidIdentifierException e) {
                throw e;
            } catch (final Throwable e) {
                throw new RuntimeException(e);
            }
        }

        return Identifier.of(id);
    }

    public static Identifier constructIdentifier(final String namespace, final String path) {
        if (CONSTRUCT_ID_FROM_STRINGS != null) {
            try {
                return (Identifier) CONSTRUCT_ID_FROM_STRINGS.invoke(namespace, path);
            } catch (final InvalidIdentifierException e) {
                throw e;
            } catch (final Throwable e) {
                throw new RuntimeException(e);
            }
        }

        return Identifier.of(namespace, path);
    }


}
