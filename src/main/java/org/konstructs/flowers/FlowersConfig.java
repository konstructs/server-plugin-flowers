package org.konstructs.flowers;

import java.util.List;
import java.util.ArrayList;

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
