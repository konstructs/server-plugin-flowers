package org.konstructs.flowers;

import java.util.Map;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

import akka.actor.ActorRef;
import akka.actor.Props;

import com.typesafe.config.Config;

import konstructs.plugin.KonstructsActor;
import konstructs.plugin.PluginConstructor;
import konstructs.api.*;
import konstructs.api.messages.*;

public class FlowersPlugin extends KonstructsActor {
    private final FlowersConfig config;
    private final List<BlockTypeId> growsOn;
    private final BlockTypeId flower;
    private final int randomGrowth;
    private final Random random = new Random();
    private float speed = GlobalConfig.DEFAULT_SIMULATION_SPEED;

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
            pos.withY(pos.getY() - config.getSeedHeightDifference());
        Position end =
            new Position(pos.getX() + 1,
                         pos.getY() + config.getSeedHeightDifference(),
                         pos.getZ() + 1);
        // Only run query if within the possible height band
        if(start.getY() < end.getY())
            boxQuery(new Box(start, end));
    }

    void seed(Position position) {
        replaceVacuumBlock(position, Block.create(flower));
        /* Plant seeds */
        if(random.nextFloat() < config.getSeedProbability()){
            Position p = position
                .addX(random.nextInt(config.getRadi() + 1))
                .addZ(random.nextInt(config.getRadi() + 1));
            int msec = (int)((float)(config.getMinSeedDelay() * 1000 +
                                     random.nextInt(config.getRandomSeedDelay()) * 1000) / speed);
            scheduleOnce(new TryToSeed(p), msec, getSelf());
        }
    }

    @Override
    public void onBoxQueryResult(BoxQueryResult result) {
        Map<Position, BlockTypeId> placed = result.getAsMap();
        for(Map.Entry<Position, BlockTypeId> p: placed.entrySet()) {
            if(growsOn.contains(p.getValue())) {
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
            if(growsOn.contains(p.getValue().getAfter().getType()) &&
               random.nextInt(10000) <= randomGrowth) {
                tryToSeed(p.getKey());
            }
        }
    }

    @Override
    public void onGlobalConfig(GlobalConfig config) {
        speed = config.getSimulationSpeed();
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
        props(String pluginName, ActorRef universe, Config config) {
        Class currentClass = new Object() { }.getClass().getEnclosingClass();
        FlowersConfig flowersConfig =
            new FlowersConfig(config);
        return Props.create(currentClass, pluginName, universe, flowersConfig);
    }
}
