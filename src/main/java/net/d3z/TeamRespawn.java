package net.d3z;

import net.d3z.saveddata.SavedSpawnpointData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import org.jspecify.annotations.Nullable;

public final class TeamRespawn {
    private TeamRespawn() {}

    @Nullable
    public static TeleportTransition find(ServerPlayer player, TeleportTransition.PostTeleportTransition post) {
        PlayerTeam team = player.getTeam();
        if (team == null) return null;

        MinecraftServer server = player.level().getServer();
        if (server == null) return null;

        LevelData.RespawnData respawnData = SavedSpawnpointData.get(server).getSpawnpoint(team.getName());
        if (respawnData == null) return null;

        ServerLevel level = server.getLevel(respawnData.dimension());
        if (level == null) return null;

        BlockPos pos = player.adjustSpawnLocation(level, respawnData.pos());
        return new TeleportTransition(level, Vec3.atBottomCenterOf(pos), Vec3.ZERO, respawnData.yaw(), respawnData.pitch(), post);
    }
}
