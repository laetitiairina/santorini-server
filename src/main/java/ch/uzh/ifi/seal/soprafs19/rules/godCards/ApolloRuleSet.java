package ch.uzh.ifi.seal.soprafs19.rules.godCards;

import ch.uzh.ifi.seal.soprafs19.entity.Field;
import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.entity.Player;
import ch.uzh.ifi.seal.soprafs19.rules.SimpleRuleSet;
import ch.uzh.ifi.seal.soprafs19.entity.Worker;

import java.util.ArrayList;
import java.util.List;

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
                                && (!fieldAfterBackEnd.getWorker().getId().equals(ownWorker.getId()))
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

    // TODO: maybe doesn't need to be overwritten
    @Override
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

    //Worker is not Stuck when a opponent worker is on a neighboring field
    private Boolean isWorkerStuck(Game game, Worker worker) {

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

                            // or an opponent's worker
                            if (workers.contains(field.getWorker().getId())) {
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


