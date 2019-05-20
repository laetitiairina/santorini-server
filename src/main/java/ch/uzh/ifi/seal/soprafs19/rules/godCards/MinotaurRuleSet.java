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
public class MinotaurRuleSet extends SimpleRuleSet {


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
                //destination field has not your own worker and this opponent worker is not being pushed off the edge
                && (fieldAfter.getWorker() == null || (!fieldAfter.getWorker().getPlayer().getIsCurrentPlayer() &&
                (!(fieldAfter.getPosX() == 0 && fieldBefore.getPosX() == 1) && !(fieldAfter.getPosX() == 4 && fieldBefore.getPosX() == 3)
                    && !(fieldAfter.getPosY() == 0 && fieldBefore.getPosY() == 1) && !(fieldAfter.getPosY() == 4 && fieldBefore.getPosY() == 3))))
                // destination field has no dome
                && (!fieldAfterBackEnd.getHasDome())) {
            //check if blocks in after field is maximum 1 higher
            return (fieldAfter.getBlocks() <= fieldBefore.getBlocks() + 1);
        }
        return false;
    }

}
