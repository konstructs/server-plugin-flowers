package org.konstructs.flowers;

import java.util.Map;
import java.util.Random;

import akka.actor.ActorRef;
import akka.actor.Props;

import konstructs.plugin.KonstructsActor;
import konstructs.api.*;
import konstructs.api.messages.BoxQueryResult;

class CanAFlowerGrowHere extends KonstructsActor {
    private final Position position;
    private final FlowersConfig config;
    private final BlockTypeId flower;
    private final Random random = new Random();

    public CanAFlowerGrowHere(ActorRef universe, Position position, FlowersConfig config) {
        super(universe);
        this.position = position;
        this.config = config;
        this.flower = config.getFlower();
        query(config.getRadi());
    }

    private void query(int radi) {
        Position start = position
            .subtractX(radi)
            .subtractZ(radi);
        Position end = position
            .addX(radi + 1)
            .addY(1)
            .addZ(+ radi + 1);
        boxQuery(new Box(start, end));
    }

    private void grow() {
        replaceVacuumBlock(position, Block.create(flower));
        /* Plant seeds */
        int seeds = Math.max(config.getMinSeeds(), random.nextInt(config.getMaxSeeds() + 1));
        for(int i = 0; i < seeds; i++) {
            Position p = position
                .addX(random.nextInt(config.getRadi() + 1))
                .addZ(random.nextInt(config.getRadi() + 1));
            int msec = config.getMinSeedDelay() * 1000 +
                random.nextInt(config.getRandomSeedDelay()) * 1000;
            scheduleOnce(new FlowersPlugin.TryToSeed(p), msec, getContext().parent());
        }

        getContext().stop(getSelf()); /* We are done, let's die*/
    }

    @Override
    public void onBoxQueryResult(BoxQueryResult result) {
        for(Map.Entry<Position, BlockTypeId> p: result.getAsMap().entrySet()) {
            if(!(p.getValue().equals(BlockTypeId.VACUUM) || // Ignore vacuum
                 p.getValue().equals(flower))) { // Ignore leaves from the same sort of tree
                getContext().stop(getSelf()); /* We are done, let's die*/
                return;
            }
        }
        grow();
    }

    public static Props props(ActorRef universe, Position start, FlowersConfig config) {
        return Props.create(CanAFlowerGrowHere.class, universe, start, config);
    }
}
