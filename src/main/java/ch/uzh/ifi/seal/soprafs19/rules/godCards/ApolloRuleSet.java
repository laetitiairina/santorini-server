package ch.uzh.ifi.seal.soprafs19.rules.godCards;

import ch.uzh.ifi.seal.soprafs19.entity.Field;
import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.entity.Player;
import ch.uzh.ifi.seal.soprafs19.rules.SimpleRuleSet;
import ch.uzh.ifi.seal.soprafs19.entity.Worker;
import org.springframework.context.annotation.Primary;
import org.springframework.jca.work.WorkManagerTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
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
            blockBefore = fieldBefore.getBlocks();
            blockAfter = fieldAfter.getBlocks();

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
            for (Field field : neighbouringFields(before, xBefore, yBefore)) {
                if(xAfter == field.getPosX() && yAfter == field.getPosY() && isValidMove(1)) {
                    return true;
                }
            }
        }
        return false;
    }

    //Worker is not Stuck when a opponent worker is on a neighboring field
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
    protected Boolean isValidMove(int difference) {
        // origin field had a worker
        if ((fieldBeforeBackEnd.getWorker() != null)
                //destination field has not your own worker
                && (fieldAfterBackEnd.getWorker() == null || !fieldAfterBackEnd.getWorker().getId().equals(ownWorker.getId()))
                // destination field has no dome
                && (!fieldAfterBackEnd.getHasDome())
                && (fieldBeforeBackEnd.getBlocks() == blockBefore)
                && (fieldAfterBackEnd.getBlocks() == blockAfter)) {
            // checks if number of blocks is within the game's limitations
            if (blockAfter >= 0 && blockAfter <= 3) {

                //check if blocks in after field is maximum 1 higher
                return (blockAfter <= blockBefore + difference);
            }
        }
        return false;
    }

}


