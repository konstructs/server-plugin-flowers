package org.konstructs.flowers;

import java.util.List;
import java.util.ArrayList;

import konstructs.api.BlockTypeId;

public class FlowersConfig {
    private final BlockTypeId flower;
    private final List<BlockTypeId> growsOn;
    private final int seedHeightDifference;
    private final int maxSeedHeight;
    private final int minSeedHeight;
    private final int radi;
    private final int minSeedDelay;
    private final int randomSeedDelay;
    private final int maxSeeds;
    private final int minSeeds;
    private final int randomGrowth;

    FlowersConfig(String flower,
                 List<String> growsOn,
                 int seedHeightDifference,
                 int maxSeedHeight,
                 int minSeedHeight,
                 int radi,
                 int minSeedDelay,
                 int randomSeedDelay,
                 int maxSeeds,
                 int minSeeds,
                 int randomGrowth) {
        this.flower = BlockTypeId.fromString(flower);
        this.growsOn = new ArrayList<>();
        for(String t: growsOn) {
            this.growsOn.add(BlockTypeId.fromString(t));
        }
        this.seedHeightDifference = seedHeightDifference;
        this.maxSeedHeight = maxSeedHeight;
        this.minSeedHeight = minSeedHeight;
        this.radi = radi;
        this.minSeedDelay = minSeedDelay;
        this.randomSeedDelay = randomSeedDelay;
        this.maxSeeds = maxSeeds;
        this.minSeeds = minSeeds;
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
    public int getMaxSeedHeight() {
        return maxSeedHeight;
    }
    public int getMinSeedHeight() {
        return minSeedHeight;
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
    public int getMinSeeds() {
        return minSeeds;
    }
    public int getMaxSeeds() {
        return maxSeeds;
    }
    public int getRandomGrowth() {
        return randomGrowth;
    }
}
