package ch.uzh.ifi.seal.soprafs19.rules.godCards;

import ch.uzh.ifi.seal.soprafs19.entity.Field;
import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.entity.Player;
import ch.uzh.ifi.seal.soprafs19.rules.SimpleRuleSet;
import ch.uzh.ifi.seal.soprafs19.entity.Worker;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@Transactional
public class ApolloRuleSet extends SimpleRuleSet {

    // difference to parent class: can only not move on fields which are occupied with own worker
    @Override
    public Boolean checkMovePhase(Game before, Game after) {
        boolean isValid = false;

        Worker ownWorker = null;

        Field fieldBefore = null;
        Field fieldAfter = null;

        Field fieldBeforeBackEnd = null;
        Field fieldAfterBackEnd = null;

        int xBefore = -1, yBefore = -1, xAfter = -1, yAfter = -1;

        // adds the position of the two fields to the array and declares which is the before and after field
        for (Field field : after.getBoard().getFields()) {
            if (field.getWorker() != null) {
                fieldAfter = field;
                xAfter = fieldAfter.getPosX();
                yAfter = fieldAfter.getPosY();
            } else {
                fieldBefore = field;
                xBefore = fieldBefore.getPosX();
                yBefore = fieldBefore.getPosY();
            }
        }

        // faulty information by front-end
        if (fieldAfter == null || fieldBefore == null) {
            return false;
        }

        for (Field field : before.getBoard().getFields()) {
            if ((field.getPosX() == xBefore) && (field.getPosY() == yBefore)) {
                fieldBeforeBackEnd = field;
            }
        }

        for (Field field : before.getBoard().getFields()) {
            if ((field.getPosX() == xAfter) && (field.getPosY() == yAfter)) {
                fieldAfterBackEnd = field;
            }
        }

        // faulty information by front-end
        if (fieldBeforeBackEnd == null || fieldAfterBackEnd == null) {
            return false;
        }

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

        int blockBefore = fieldBefore.getBlocks();
        int blockAfter = fieldAfter.getBlocks();

        // checks if the Worker's position is within the board's limitations
        if (xAfter >= 0 && xAfter <= 4 && yAfter >= 0 && yAfter <= 4) {

            // move 1 or 0 fields on X-axis
            if ((xAfter == xBefore) || (xAfter == xBefore - 1) || (xAfter == xBefore + 1)) {

                // move 1 or 0 fields on Y-axis
                if ((yAfter == yBefore) || (yAfter == yBefore - 1) || (yAfter == yBefore + 1)) {

                    // not allowed to stay on same field
                    if ((xAfter != xBefore) || (yAfter != yBefore)) {

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
                                if ((blockAfter <= blockBefore + 1)) {
                                    isValid = true;
                                }
                            }
                        }
                    }
                }
            }
        }

        return isValid;

    }

    //Worker is not Stuck when a opponent worker is on a neighboring field
    @Override
    public Boolean isWorkerStuck(Game game, Worker worker) {

        int posX = worker.getField().getPosX();
        int posY = worker.getField().getPosY();

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

        // finds neighbouring fields
        for (Field field : game.getBoard().getFields()) {

            // on x axis
            if (field.getPosX() == posX - 1 || field.getPosX() == posX || field.getPosX() == posX + 1) {

                // on y axis
                if (field.getPosY() == posY - 1 || field.getPosY() == posY || field.getPosY() == posY + 1) {

                    // not the same field
                    if (field.getPosX() != posX || field.getPosY() != posY) {

                        // it's free, if it has no dome, max. one block more than the worker's current field,
                        if (!field.getHasDome() && (field.getBlocks() - 1) <= (worker.getField().getBlocks())) {
                            // a field is free
                            if (field.getWorker() == null || workers.contains(field.getWorker().getId())) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

}


