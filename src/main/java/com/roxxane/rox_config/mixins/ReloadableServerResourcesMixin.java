package com.roxxane.rox_config.mixins;

import com.roxxane.rox_config.Config;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.flag.FeatureFlagSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ReloadableServerResources.class)
abstract class ReloadableServerResourcesMixin {
	@SuppressWarnings("CallToPrintStackTrace")
	@Inject(method = "loadResources", at = @At("HEAD"))
	private static void loadResourcesMixin(ResourceManager resourceManager, RegistryAccess.Frozen registryAccess,
		FeatureFlagSet enabledFeatures, Commands.CommandSelection commandSelection, int functionCompilationLevel,
		Executor backgroundExecutor, Executor gameExecutor,
		CallbackInfoReturnable<CompletableFuture<ReloadableServerResources>> cir
	) {
		for (var config : Config.getConfigs())
			try {
				config.reload();
			} catch (Exception exception) {
				config.logger.warn("Could not reload config \"{}\"", config.id);
				exception.printStackTrace();
			}
	}
}