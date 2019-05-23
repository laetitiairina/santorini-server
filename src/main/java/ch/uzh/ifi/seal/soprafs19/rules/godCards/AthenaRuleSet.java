package ch.uzh.ifi.seal.soprafs19.rules.godCards;

import ch.uzh.ifi.seal.soprafs19.entity.Field;
import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.entity.Worker;
import ch.uzh.ifi.seal.soprafs19.rules.SimpleRuleSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class AthenaRuleSet extends SimpleRuleSet {

    @Override
    public Boolean checkMovePhaseOpponent(Game before, Game after) {
        Boolean isValidMove = true;
        if (before.getBlockDifference() > 0) {
            checkMovePhase(before, after);
            isValidMove = fieldAfter.getBlocks() - fieldBefore.getBlocks() <= 0;
        }
        return isValidMove;
    }

    @Override
    public Boolean isWorkerStuck(Game game, Worker worker) {
        for (Field field : neighbouringFields(game, worker.getField().getPosX(), worker.getField().getPosY())) {
            // it's free, if it has no dome, no worker
            if (!field.getHasDome() && field.getWorker() == null) {

                // or max. one block more than the worker's current field
                int heightDiff = field.getBlocks() - worker.getField().getBlocks();
                if (heightDiff < 1 || heightDiff == 1 && game.getBlockDifference() < 1) {
                    // a field is free
                    return false;
                }
            }
        }
        return true;
    }

}
