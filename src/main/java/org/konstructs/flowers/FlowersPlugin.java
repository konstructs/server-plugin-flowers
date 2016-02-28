package org.konstructs.flowers;

import java.util.Map;
import java.util.Random;

import akka.actor.ActorRef;
import akka.actor.Props;
import konstructs.plugin.KonstructsActor;
import konstructs.plugin.PluginConstructor;
import konstructs.plugin.Config;
import konstructs.api.*;

public class FlowersPlugin extends KonstructsActor {
    private final FlowersConfig config;
    private final BlockTypeId growsOn;
    private final BlockTypeId flower;
    private final int randomGrowth;
    private final Random random = new Random();

    public static class TryToSeed {
        private final Position position;

        public TryToSeed(Position position) {
            this.position = position;
        }

        public Position getPosition() {
            return position;
        }
    }

    public FlowersPlugin(String name, ActorRef universe, FlowersConfig config) {
        super(universe);
        this.config = config;
        this.growsOn = config.getGrowsOn();
        this.flower = config.getFlower();
        this.randomGrowth = config.getRandomGrowth();
    }

    void tryToSeed(Position pos) {
        Position start =
            new Position(pos.x(),
                         Math.max(pos.y() - config.getSeedHeightDifference(),
                                  config.getMinSeedHeight()),
                         pos.z());
        Position end =
            new Position(pos.x() + 1,
                         Math.min(pos.y() + config.getSeedHeightDifference(),
                                  config.getMaxSeedHeight()),
                         pos.z() + 1);
        boxQuery(start, end);
    }

    void seed(Position pos) {
        getContext().actorOf(CanAFlowerGrowHere.props(getUniverse(), pos, config));
    }

    @Override
    public void onBoxQueryResult(BoxQueryResult result) {
        Map<Position, BlockTypeId> placed = result.result().toPlaced();
        for(Map.Entry<Position, BlockTypeId> p: placed.entrySet()) {
            if(p.getValue().equals(growsOn)) {
                Position pos = p.getKey().incY(1);
                BlockTypeId above = placed.get(pos);
                if(above != null && above.equals(BlockTypeId.vacuum())) {
                    seed(pos);
                    return;
                }
            }
        }
    }

    @Override
    public void onEventBlockUpdated(EventBlockUpdated update) {
        for(Map.Entry<Position, BlockTypeId> p: update.blocks().entrySet()) {
            if(p.getValue().equals(growsOn) &&
               random.nextInt(1000) <= randomGrowth) {
                tryToSeed(p.getKey());
            }
        }
    }

    @Override
    public void onEventBlockRemoved(EventBlockRemoved block) {}

    @Override
    public void onReceive(Object message) {
        if(message instanceof TryToSeed) {
            TryToSeed seed = (TryToSeed)message;
            tryToSeed(seed.getPosition());
        } else {
            super.onReceive(message); // Handle konstructs messages
        }
    }

    @PluginConstructor
    public static Props
        props(
              String pluginName,
              ActorRef universe,
              @Config(key = "flower-block") String flower,
              @Config(key = "grows-on") String growsOn,
              @Config(key = "max-seed-height-difference") int seedHeightDifference,
              @Config(key = "max-seed-height") int maxSeedHeight,
              @Config(key = "min-seed-height") int minSeedHeight,
              @Config(key = "seed-radi") int radi,
              @Config(key = "min-seed-delay") int minSeedDelay,
              @Config(key = "random-seed-delay") int randomSeedDelay,
              @Config(key = "max-seeds") int maxSeeds,
              @Config(key = "min-seeds") int minSeeds,
              @Config(key = "random-growth") int randomGrowth
              ) {
        Class currentClass = new Object() { }.getClass().getEnclosingClass();
        FlowersConfig config =
            new FlowersConfig(flower,
                              growsOn,
                              seedHeightDifference,
                              maxSeedHeight,
                              minSeedHeight,
                              radi,
                              minSeedDelay,
                              randomSeedDelay,
                              maxSeeds,
                              minSeeds,
                              randomGrowth);
        return Props.create(currentClass, pluginName, universe, config);
    }
}
