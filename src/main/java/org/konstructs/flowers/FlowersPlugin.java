package org.konstructs.flowers;

import java.util.Map;
import java.util.Random;

import akka.actor.ActorRef;
import akka.actor.Props;
import konstructs.plugin.KonstructsActor;
import konstructs.plugin.PluginConstructor;
import konstructs.plugin.Config;
import konstructs.api.*;
import konstructs.api.messages.BlockUpdateEvent;
import konstructs.api.messages.BoxQueryResult;

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
            pos.withY(Math.max(pos.getY() - config.getSeedHeightDifference(),
                               config.getMinSeedHeight()));
        Position end =
            new Position(pos.getX() + 1,
                         Math.min(pos.getY() + config.getSeedHeightDifference(),
                                  config.getMaxSeedHeight()),
                         pos.getZ() + 1);
        // Only run query if within the possible height band
        if(start.getY() < end.getY())
            boxQuery(new Box(start, end));
    }

    void seed(Position pos) {
        getContext().actorOf(CanAFlowerGrowHere.props(getUniverse(), pos, config));
    }

    @Override
    public void onBoxQueryResult(BoxQueryResult result) {
        Map<Position, BlockTypeId> placed = result.getAsMap();
        for(Map.Entry<Position, BlockTypeId> p: placed.entrySet()) {
            if(p.getValue().equals(growsOn)) {
                Position pos = p.getKey().addY(1);
                BlockTypeId above = placed.get(pos);
                if(above != null && above.equals(BlockTypeId.VACUUM)) {
                    seed(pos);
                    return;
                }
            }
        }
    }

    @Override
    public void onBlockUpdateEvent(BlockUpdateEvent event) {
        for(Map.Entry<Position, BlockUpdate> p: event.getUpdatedBlocks().entrySet()) {
            if(p.getValue().getAfter().getType().equals(growsOn) &&
               random.nextInt(1000) <= randomGrowth) {
                tryToSeed(p.getKey());
            }
        }
    }

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
