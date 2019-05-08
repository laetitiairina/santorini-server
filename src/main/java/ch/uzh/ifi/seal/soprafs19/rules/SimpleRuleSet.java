package ch.uzh.ifi.seal.soprafs19.rules;

import ch.uzh.ifi.seal.soprafs19.entity.Field;
import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.entity.Player;
import ch.uzh.ifi.seal.soprafs19.entity.Worker;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Primary
@Component
@Transactional
public class SimpleRuleSet implements IRuleSet {

    public Boolean checkMovePhase(Game before, Game after) {

        boolean isValid = false;

        Field fieldBefore = null;
        Field fieldAfter = null;

        Field fieldBeforeBackEnd = null;
        Field fieldAfterBackEnd = null;

        int xBefore = -1, yBefore = -1, xAfter = -1, yAfter = -1;

        // adds the position of the two fields to the array and declares which is the before and after field
        for (Field field : after.getBoard().getFields()) {
            if (field.getWorker() != null) {
                fieldAfter = field;
                xAfter =fieldAfter.getPosX();
                yAfter = fieldAfter.getPosY();
            } else {
                fieldBefore = field;
                xBefore = fieldBefore.getPosX();
                yBefore = fieldBefore.getPosY();
            }
        }

        // faulty information by front-end
        if (fieldAfter == null || fieldBefore ==  null) {
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
                                // destination field is unoccupied
                                && (fieldAfterBackEnd.getWorker() == null)
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

    public Boolean checkBuildPhase(Game before, Game after) {

        boolean isValid = false;

        Field fieldAfterBuilt = null;
        Field fieldBuiltOnBackEnd = null;

        int posWorkerX = -1, posWorkerY = -1;
        int posBuiltFieldX = -1, posBuiltFieldY = -1;

        //adds the position of the Field that has been sent from the front-end
        for (Field field : after.getBoard().getFields()) {
            if (field != null) {
                fieldAfterBuilt = field;
                posBuiltFieldX = fieldAfterBuilt.getPosX();
                posBuiltFieldY = fieldAfterBuilt.getPosY();
            }
        }

        // if front-end sent no field at all, or faulty one
        if (fieldAfterBuilt == null
                || (posBuiltFieldX < 0) || (posBuiltFieldX > 4) || (posBuiltFieldY < 0)|| (posBuiltFieldY > 4)) {
            return false;
        }

        //gets the Position of the current Worker which is building a block
        for (Player p : before.getPlayers()) {
            if (p.getIsCurrentPlayer()) {
                for (Worker w : p.getWorkers()) {
                    if (w.getIsCurrentWorker()) {
                        posWorkerX = w.getField().getPosX();
                        posWorkerY = w.getField().getPosY();
                    }
                }
            }
        }

        for (Field field : before.getBoard().getFields()) {
            if (field.getPosX() == posBuiltFieldX && field.getPosY() == posBuiltFieldY) {
                fieldBuiltOnBackEnd = field;
            }
        }

        // faulty information by front-end
        if (fieldBuiltOnBackEnd == null) {
            return false;
        }

        // Built on 1 or 0 fields next to the worker on X-axis
        if ((posBuiltFieldX == posWorkerX) || (posBuiltFieldX == posWorkerX - 1) || (posBuiltFieldX == posWorkerX + 1)) {

            // Built on 1 or 0 fields next to the worker on Y-axis
            if ((posBuiltFieldY == posWorkerY) || (posBuiltFieldY == posWorkerY - 1) || (posBuiltFieldY == posWorkerY + 1)) {

                // can not built on the same field as the worker
                if ((posBuiltFieldX != posWorkerX) || (posBuiltFieldY != posWorkerY)) {

                    // the field does not have a worker and no dome
                    if (((fieldBuiltOnBackEnd.getWorker() == null) && !(fieldBuiltOnBackEnd.getHasDome())) &&
                            // either the player added only a block
                            ((fieldBuiltOnBackEnd.getBlocks() == fieldAfterBuilt.getBlocks() - 1) && (fieldAfterBuilt.getBlocks() <= 3)
                                    && !fieldAfterBuilt.getHasDome())
                            ||
                            // or the player added a dome
                            ((fieldBuiltOnBackEnd.getBlocks() == 3) && fieldAfterBuilt.getHasDome())) {

                        isValid = true;
                    }
                }
            }
        }

        return isValid;
    }

    public Boolean checkWinCondition(Game game) {

        // checking for both players
        for (Player player : game.getPlayers()) {

            // number of Workers of a Player, who can't move anymore
            int stuckWorkers = 0;

            // check positions of Workers
            for (Worker worker : player.getWorkers()) {

                // if worker has been at least dropped on the board
                if (worker.getField() != null) {

                    // win condition
                    if ((worker.getField().getBlocks() == 3 && !worker.getField().getHasDome())) {
                        // immediately return
                        worker.getPlayer().setIsCurrentPlayer(true);
                        return true;
                    }
                    // lose condition
                    else if (isWorkerStuck(game, worker)) {
                        stuckWorkers++;
                    }
                }
            }

            // if both workers are stuck
            if (stuckWorkers == 2) {
                // set current Player for front-end
                for (Player p : game.getPlayers()) {
                    player.setIsCurrentPlayer(!p.getId().equals(player.getId()));
                }
                return true;
            }

        }
        return false;
    }


    public Boolean hasRuleForOpponentsTurn() {
        return false;
    }

    /**
     * Method that Checks if a Worker Can not move anymore
     * @param game
     * @param worker
     * @return
     */
    public Boolean isWorkerStuck(Game game, Worker worker) {

        int posX = worker.getField().getPosX();
        int posY = worker.getField().getPosY();

        // finds neighbouring fields
        for (Field field : game.getBoard().getFields()) {

            // on x axis
            if (field.getPosX() == posX - 1 || field.getPosX() == posX || field.getPosX() == posX + 1) {

                // on y axis
                if (field.getPosY() == posY - 1 || field.getPosY() == posY  || field.getPosY() == posY+ 1) {

                    // not the same field
                    if (field.getPosX() != posX || field.getPosY() != posY) {

                        // it's free, if it has no dome, no worker
                        if (!field.getHasDome() && field.getWorker() == null) {

                            // or max. one block more than the worker's current field
                            if ((field.getBlocks() - 1) <= (worker.getField().getBlocks())) {
                                // a field is free
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
