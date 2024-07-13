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
package me.mightyknight.sd.multiversion_mixin.version;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.Version;

public class PehkuiVersionUtils
{
    private static final List<Predicate<String>> VERSION_PREDICATES = new ArrayList<>();

    static
    {
        final int[][] ranges =
                {
                        {1, 14, 4},
                        {1, 15, 2},
                        {1, 16, 5},
                        {1, 17, 1},
                        {1, 18, 2},
                        {1, 19, 4},
                        {1, 20, 6},
                        {1, 21, 0},
                };

        final String prefix = ".compat%s%s";

        for (final int[] range : ranges)
        {
            final int major = range[0];
            final int minor = range[1];

            final String predicatePrefix = String.format(prefix, major, minor);
            final String predicateMinor = predicatePrefix + ".";
            final String predicateMinorPlus = predicatePrefix + "plus.";
            final String predicateMinorMinus = predicatePrefix + "minus.";
            final String predicateMinorZero = predicatePrefix + "0.";
            final String predicateMinorZeroMinus = predicatePrefix + "0minus.";

            VERSION_PREDICATES.add(n -> n.contains(predicateMinor) && PehkuiVersionUtils.MINOR != minor);
            VERSION_PREDICATES.add(n -> n.contains(predicateMinorPlus) && PehkuiVersionUtils.MINOR < minor);
            VERSION_PREDICATES.add(n -> n.contains(predicateMinorMinus) && PehkuiVersionUtils.MINOR > minor);
            VERSION_PREDICATES.add(n -> n.contains(predicateMinorZero) && (PehkuiVersionUtils.MINOR != minor || PehkuiVersionUtils.PATCH != 0));
            VERSION_PREDICATES.add(n -> n.contains(predicateMinorZeroMinus) && (PehkuiVersionUtils.MINOR > minor || (PehkuiVersionUtils.MINOR == minor && PehkuiVersionUtils.PATCH > 0)));

            final int maxPatch = range[2];

            for (int i = 1; i <= maxPatch; i++)
            {
                final int patch = i;

                final String predicatePatch = predicatePrefix + patch + ".";
                final String predicatePatchPlus = predicatePrefix + patch + "plus.";
                final String predicatePatchMinus = predicatePrefix + patch + "minus.";

                VERSION_PREDICATES.add(n -> n.contains(predicatePatch) && (PehkuiVersionUtils.MINOR != minor || PehkuiVersionUtils.PATCH != patch));
                VERSION_PREDICATES.add(n -> n.contains(predicatePatchPlus) && (PehkuiVersionUtils.MINOR < minor || (PehkuiVersionUtils.MINOR == minor && PehkuiVersionUtils.PATCH < patch)));
                VERSION_PREDICATES.add(n -> n.contains(predicatePatchMinus) && (PehkuiVersionUtils.MINOR > minor || (PehkuiVersionUtils.MINOR == minor && PehkuiVersionUtils.PATCH > patch)));
            }
        }
    }

    public static boolean shouldApplyCompatibilityMixin(String mixinClassName)
    {
        if (mixinClassName.contains(".compat"))
        {
            for (final Predicate<String> predicate : VERSION_PREDICATES)
            {
                if (predicate.test(mixinClassName))
                {
                    return false;
                }
            }
        }

        return true;
    }

    @Nullable
    public static final SemanticVersion MINECRAFT_VERSION = lookupMinecraftVersion();
    public static final int MAJOR = getVersionComponent(0);
    public static final int MINOR = getVersionComponent(1);
    public static final int PATCH = getVersionComponent(2);

    private static SemanticVersion lookupMinecraftVersion()
    {
        final Version version = FabricLoader.getInstance().getModContainer("minecraft").get().getMetadata().getVersion();

        return (SemanticVersion) (version instanceof SemanticVersion ? version : null);
    }

    private static int getVersionComponent(int pos)
    {
        return MINECRAFT_VERSION != null ? MINECRAFT_VERSION.getVersionComponent(pos) : -1;
    }
}
