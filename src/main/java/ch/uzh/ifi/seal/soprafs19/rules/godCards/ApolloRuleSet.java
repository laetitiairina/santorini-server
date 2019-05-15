package ch.uzh.ifi.seal.soprafs19.rules.godCards;

import ch.uzh.ifi.seal.soprafs19.entity.Field;
import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.entity.Player;
import ch.uzh.ifi.seal.soprafs19.rules.SimpleRuleSet;
import ch.uzh.ifi.seal.soprafs19.entity.Worker;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@Transactional
public class ApolloRuleSet extends SimpleRuleSet {

    // extra
    private Worker ownWorker;

    // difference to parent class: can only not move on fields which are occupied with own worker
    @Override
    public Boolean checkMovePhase(Game before, Game after) {

        if (setFieldsAfterMovePhase(after) && setFieldsBeforeMovePhase(before)) {
            // get Worker of current Player, which is not being moved
            for (Player p : before.getPlayers()) {
                if (p.getIsCurrentPlayer()) {
                    for (Worker w : p.getWorkers()) {
                        if (w != null && !fieldBeforeBackEnd.getWorker().getId().equals(w.getId())) {
                            ownWorker = w;
                        }
                    }
                }
            }

            // faulty info from front-end
            if (ownWorker == null) {
                return false;
            }

            // check if it can move
            return isFieldFree(before, fieldBeforeBackEnd, fieldAfterBackEnd, false);
        }
        return false;
    }

    //Worker is not anymore Stuck when a opponent worker is on a neighboring field
    @Override
    public Boolean isWorkerStuck(Game game, Worker worker) {
        List<Long> workers = new ArrayList<>();

        // find Workers of opponent
        for (Player p : game.getPlayers()) {
            if (!p.getIsCurrentPlayer()) {
                for (Worker w : p.getWorkers()) {
                    if (w != null) {
                        workers.add(w.getId());
                    }
                }
            }
        }

        for (Field field : neighbouringFields(game, worker.getField().getPosX(), worker.getField().getPosY())) {
            // it's free, if it has no dome, max. one block more than the worker's current field,
            if (!field.getHasDome() && (field.getBlocks() - 1) <= (worker.getField().getBlocks())) {
                // a field is free
                if (field.getWorker() == null || workers.contains(field.getWorker().getId())) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected Boolean isValidMove(boolean isSecondMove, Field fieldBefore, Field fieldAfter) {
        // origin field had a worker
        if ((fieldBefore.getWorker() != null) || isSecondMove
                //destination field has not your own worker
                && (fieldAfter.getWorker() == null || !fieldAfter.getWorker().getId().equals(ownWorker.getId()))
                // destination field has no dome
                && (!fieldAfterBackEnd.getHasDome())) {
            //check if blocks in after field is maximum 1 higher
            return (fieldAfter.getBlocks() <= fieldBefore.getBlocks() + 1);
        }
        return false;
    }

}


