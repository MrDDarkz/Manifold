package net.d3z.saveddata;

import com.mojang.serialization.Codec;
import net.d3z.Manifold;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.LevelData;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SavedSpawnpointData extends SavedData {
    private final Map<String, LevelData.RespawnData> spawnpoints = new HashMap<>();

    public SavedSpawnpointData(Map<String, LevelData.RespawnData> teamSpawnpoints) {
        this.spawnpoints.putAll(teamSpawnpoints);
    }

    public SavedSpawnpointData() {};

    public Map<String, LevelData.RespawnData> getSpawnpoints() {
        return Collections.unmodifiableMap(spawnpoints);
    }

    public LevelData.RespawnData getSpawnpoint(String teamName) {
        return spawnpoints.get(teamName);
    }

    private static final Codec<SavedSpawnpointData> CODEC =
        Codec.unboundedMap(Codec.STRING, LevelData.RespawnData.CODEC)
            .xmap(
                SavedSpawnpointData::new,
                SavedSpawnpointData::getSpawnpoints
            );

    private static final SavedDataType<SavedSpawnpointData> TYPE = new SavedDataType<>(
        Manifold.id("manifold_spawnpoints"),
        SavedSpawnpointData::new,
        CODEC,
        null
    );

    public static SavedSpawnpointData get(MinecraftServer server) {
        ServerLevel level = server.getLevel(ServerLevel.OVERWORLD);

        if (level == null) {
            return new SavedSpawnpointData();
        }

        return level.getDataStorage().computeIfAbsent(TYPE);
    }

    public void setSpawnpoint(String teamName, LevelData.RespawnData respawnData) {
        spawnpoints.put(teamName, respawnData);
        setDirty();
        Manifold.LOGGER.info("{} spawnpoint saved", teamName);
    }

    public void deleteSpawnpoint(String teamName) {
        if (spawnpoints.remove(teamName) != null) {
            setDirty();
            Manifold.LOGGER.info("{} spawnpoint removed", teamName);
        }
    }
}
