package org.konstructs.flowers;

import java.util.List;
import java.util.ArrayList;

import com.typesafe.config.Config;

import konstructs.api.BlockTypeId;

public class FlowersConfig {
    private final BlockTypeId flower;
    private final List<BlockTypeId> growsOn;
    private final int seedHeightDifference;
    private final int radi;
    private final int minSeedDelay;
    private final int randomSeedDelay;
    private final float seedProbability;
    private final int randomGrowth;

    private static List<String> growsOnTypes(Config config) {
        List<String> growsOnList = new ArrayList<>();
        Config growsOn = config.getConfig("grows-on");
        for(String k: growsOn.root().keySet()) {
            String type = growsOn.getString(k);
            if(type != null)
                growsOnList.add(type);
        }
        return growsOnList;
    }

    FlowersConfig(Config config) {
        this(config.getString("flower-block"),
             growsOnTypes(config),
             config.getInt("max-seed-height-difference"),
             config.getInt("seed-radi"),
             config.getInt("min-seed-delay"),
             config.getInt("random-seed-delay"),
             config.getInt("seed-probability"),
             config.getInt("random-growth"));
    }

    FlowersConfig(String flower,
                  List<String> growsOn,
                  int seedHeightDifference,
                  int radi,
                  int minSeedDelay,
                  int randomSeedDelay,
                  float seedProbability,
                  int randomGrowth) {
        this.flower = BlockTypeId.fromString(flower);
        this.growsOn = new ArrayList<>();
        for(String t: growsOn) {
            this.growsOn.add(BlockTypeId.fromString(t));
        }
        this.seedHeightDifference = seedHeightDifference;
        this.radi = radi;
        this.minSeedDelay = minSeedDelay;
        this.randomSeedDelay = randomSeedDelay;
        this.seedProbability = seedProbability;
        this.randomGrowth = randomGrowth;
    }

    public BlockTypeId getFlower() {
        return flower;
    }
    public List<BlockTypeId> getGrowsOn() {
        return growsOn;
    }
    public int getSeedHeightDifference() {
        return seedHeightDifference;
    }
    public int getRadi() {
        return radi;
    }
    public int getMinSeedDelay() {
        return minSeedDelay;
    }
    public int getRandomSeedDelay() {
        return randomSeedDelay;
    }
    public float getSeedProbability() {
        return seedProbability;
    }
    public int getRandomGrowth() {
        return randomGrowth;
    }
}
