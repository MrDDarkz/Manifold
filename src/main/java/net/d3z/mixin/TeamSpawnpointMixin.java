package net.d3z.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.d3z.TeamRespawn;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.portal.TeleportTransition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayer.class)
public class TeamSpawnpointMixin {
	@WrapOperation(
		method = "findRespawnPositionAndUseSpawnBlock",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/portal/TeleportTransition;missingRespawnBlock(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/level/portal/TeleportTransition$PostTeleportTransition;)Lnet/minecraft/world/level/portal/TeleportTransition;")
	)
	private TeleportTransition onMissingRespawnBlock(
		ServerPlayer player,
		TeleportTransition.PostTeleportTransition post,
		Operation<TeleportTransition> original
	) {
		TeleportTransition team = TeamRespawn.find(player, post);
		return team != null ? team : original.call(player,post);
	}

	@WrapOperation(
		method = "findRespawnPositionAndUseSpawnBlock",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/portal/TeleportTransition;createDefault(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/level/portal/TeleportTransition$PostTeleportTransition;)Lnet/minecraft/world/level/portal/TeleportTransition;")
	)
	private TeleportTransition onCreateDefault(
		ServerPlayer player,
		TeleportTransition.PostTeleportTransition post,
		Operation<TeleportTransition> original
	) {
		TeleportTransition team = TeamRespawn.find(player, post);
		return team != null ? team : original.call(player,post);
	}
}