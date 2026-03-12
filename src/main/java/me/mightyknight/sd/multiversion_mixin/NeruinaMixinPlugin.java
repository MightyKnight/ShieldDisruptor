/*
 * MIT License
 * Copyright (c) 2023-Present Bawnorton

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package me.mightyknight.sd.multiversion_mixin;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import me.mightyknight.sd.common.ShieldDisruptor;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.util.Annotations;

import java.io.IOException;
import java.util.List;
import java.util.Set;
public class NeruinaMixinPlugin implements IMixinConfigPlugin {

    public static boolean testClass(String className) {
        try {
            List<AnnotationNode> annotationNodes = MixinService.getService().getBytecodeProvider().getClassNode(className).visibleAnnotations;
            if (annotationNodes == null) {
                ShieldDisruptor.LOGGER.error("No annotated nodes found, check versioning");
                return true;
            }

            boolean shouldApply = true;
            for (AnnotationNode node : annotationNodes) {
                ShieldDisruptor.LOGGER.debug("Deciding application of annotated mixin: {}", node.toString());
                if (node.desc.equals(Type.getDescriptor(ConditionalMixin.class))) {
                    List<String> modids = Annotations.getValue(node, "modids");
                    boolean applyIfPresent = Annotations.getValue(node, "applyIfPresent", Boolean.TRUE);
                    if (anyModsLoaded(modids)) {
                        ShieldDisruptor.LOGGER.debug("NeruinaMixinPlugin: {} is{}being applied because {} are loaded", className, applyIfPresent ? " " : " not ", modids);
                        shouldApply = applyIfPresent;
                    } else {
                        ShieldDisruptor.LOGGER.debug("NeruinaMixinPlugin: {} is{}being applied because {} are not loaded", className, !applyIfPresent ? " " : " not ", modids);
                        shouldApply = !applyIfPresent;
                    }
                } else if (node.desc.equals(Type.getDescriptor(VersionedMixin.class))) {
                    String min = Annotations.getValue(node, "min", "");
                    String max = Annotations.getValue(node, "max", "");
                    String currentVersion = Platform.getMinecraftVersion();
                    ComparableVersion comparableVersion = new ComparableVersion(currentVersion);
                    shouldApply = evaluateVersion(className, min, max, currentVersion, comparableVersion);
                }
                if (!shouldApply) break;
            }
            return shouldApply;
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean evaluateVersion(String className, String min, String max, String currentVersion, ComparableVersion comparableVersion) {
        boolean shouldApply = true;
        if (!min.isBlank()) {
            shouldApply &= comparableVersion.compareTo(new ComparableVersion(min)) >= 0;
        }
        if (!max.isBlank()) {
            shouldApply &= comparableVersion.compareTo(new ComparableVersion(max)) <= 0;
        }
        String applicationYesNoString = shouldApply ? "" : "not ";
        ShieldDisruptor.LOGGER.info("NeruinaMixinPlugin: {} is {}being applied because version {} is {} in range ({}, {})",
                className, applicationYesNoString, currentVersion, applicationYesNoString, min, max);

        return shouldApply;
    }

    private static boolean anyModsLoaded(List<String> modids) {
        for (String modid : modids) {
            if (Platform.isModLoaded(modid)) return true;
        }
        return false;
    }

    @Override
    public void onLoad(String mixinPackage) {
        MixinExtrasBootstrap.init();
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetName, String className) {
        return testClass(className);
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
