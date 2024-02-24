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
import me.mightyknight.sd.multiversion_mixin.version.VersionString;
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
            if (annotationNodes == null) return true;

            boolean shouldApply = true;
            for (AnnotationNode node : annotationNodes) {
                if (node.desc.equals(Type.getDescriptor(ConditionalMixin.class))) {
                    List<String> modids = Annotations.getValue(node, "modids");
                    boolean applyIfPresent = Annotations.getValue(node, "applyIfPresent", Boolean.TRUE);
                    if (anyModsLoaded(modids)) {
                        ShieldDisruptor.LOGGER.debug("NeruinaMixinPlugin: " + className + " is" + (applyIfPresent ? " " : " not ") + "being applied because " + modids + " are loaded");
                        shouldApply = applyIfPresent;
                    } else {
                        ShieldDisruptor.LOGGER.debug("NeruinaMixinPlugin: " + className + " is" + (!applyIfPresent ? " " : " not ") + "being applied because " + modids + " are not loaded");
                        shouldApply = !applyIfPresent;
                    }
                }
                if (!shouldApply) return false;

                if (node.desc.equals(Type.getDescriptor(VersionedMixin.class))) {
                    String versionString = Annotations.getValue(node, "value");
                    VersionString version = new VersionString(versionString);
                    String mcVersion = Platform.getMinecraftVersion();
                    if (version.isVersionValid(mcVersion)) {
                        ShieldDisruptor.LOGGER.debug("NeruinaMixinPlugin: " + className + " is being applied because " + mcVersion + " is " + versionString);
                    } else {
                        ShieldDisruptor.LOGGER.debug("NeruinaMixinPlugin: " + className + " is not being applied because " + mcVersion + " is not " + versionString);
                        shouldApply = false;
                    }
                }
            }
            return shouldApply;
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
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
