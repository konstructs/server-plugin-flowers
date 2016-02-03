package konstructs.flowers;

import java.util.Map;
import java.util.Random;

import akka.actor.ActorRef;
import akka.actor.Props;

import konstructs.plugin.KonstructsActor;
import konstructs.api.*;

class CanAFlowerGrowHere extends KonstructsActor {
    private final Position position;
    private final FlowersConfig config;
    private final BlockTypeId flower;
    private final BlockTypeId vacuum = BlockTypeId.vacuum();
    private final Random random = new Random();

    public CanAFlowerGrowHere(ActorRef universe, Position position, FlowersConfig config) {
        super(universe);
        this.position = position;
        this.config = config;
        this.flower = config.getFlower();
        query(config.getRadi());
    }

    private void query(int radi) {
        Position start =
            new Position(position.x() - radi,
                         position.y(),
                         position.z() - radi);
        Position end =
            new Position(position.x() + radi + 1,
                         position.y() + 1,
                         position.z() + radi + 1);
        boxQuery(start, end);
    }

    private void grow() {
        replaceBlock(position, flower,
                     BlockFilterFactory.vacuum());
        /* Plant seeds */
        int seeds = Math.max(config.getMinSeeds(), random.nextInt(config.getMaxSeeds() + 1));
        for(int i = 0; i < seeds; i++) {
            Position p = new Position(position.x() + random.nextInt(config.getRadi() + 1),
                                      position.y(),
                                      position.z() + random.nextInt(config.getRadi() + 1));
            int msec = config.getMinSeedDelay() * 1000 +
                random.nextInt(config.getRandomSeedDelay()) * 1000;
            scheduleOnce(new FlowersPlugin.TryToSeed(p), msec, getContext().parent());
        }

        getContext().stop(getSelf()); /* We are done, let's die*/
    }

    @Override
    public void onBoxQueryResult(BoxQueryResult result) {
        for(Map.Entry<Position, BlockTypeId> p: result.result().toPlaced().entrySet()) {
            if(!(p.getValue().equals(vacuum) || // Ignore vacuum
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
