package net.d3z.command;

import com.mojang.brigadier.context.CommandContext;
import net.d3z.saveddata.SavedSpawnpointData;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.TeamArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.RotationArgument;
import net.minecraft.commands.arguments.coordinates.WorldCoordinates;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.scores.PlayerTeam;

public class TeamCommands {
    private static int setSpawn(final CommandSourceStack source, final PlayerTeam team, final BlockPos pos, final Coordinates rotation) {
        ServerLevel level = source.getLevel();
        Vec2 rotationVector = rotation.getRotation(source);
        float yaw = rotationVector.y;
        float pitch = rotationVector.x;
        LevelData.RespawnData respawnData = LevelData.RespawnData.of(level.dimension(), pos, yaw, pitch);
        SavedSpawnpointData.get(source.getServer()).setSpawnpoint(team.getName(), respawnData);
        source.sendSuccess(
            () -> Component.literal("Spawnpoint for ")
                .append(team.getFormattedDisplayName())
                .append(String.format(" set to %d, %d, %d [%.1f, %.1f] %s",
                    pos.getX(), pos.getY(), pos.getZ(), respawnData.yaw(), respawnData.pitch(), level.dimension().identifier())
                ),
            false
        );
        return 1;
    }

    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("team")
                .then(Commands.literal("modify")
                    .then(Commands.argument("team", TeamArgument.team())
                        .then(Commands.literal("spawnpoint")
                            .executes(
                                c -> setSpawn(
                                    c.getSource(),
                                    TeamArgument.getTeam(c, "team"),
                                    BlockPos.containing(c.getSource().getPosition()),
                                    WorldCoordinates.ZERO_ROTATION
                                ))
                            .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .executes(
                                    c -> setSpawn(
                                        c.getSource(),
                                        TeamArgument.getTeam(c, "team"),
                                        BlockPosArgument.getSpawnablePos(c, "pos"),
                                        WorldCoordinates.ZERO_ROTATION
                                    ))
                                .then(Commands.argument("rotation", RotationArgument.rotation())
                                    .executes(
                                        c -> setSpawn(
                                            c.getSource(),
                                            TeamArgument.getTeam(c, "team"),
                                            BlockPosArgument.getSpawnablePos(c, "pos"),
                                            RotationArgument.getRotation(c, "rotation")
                                        ))
                                )
                            )
                        )
                    )
                )
            );
        });
    }
}
