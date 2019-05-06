package ch.uzh.ifi.seal.soprafs19.rules.godCards;

import ch.uzh.ifi.seal.soprafs19.entity.Field;
import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.entity.Player;
import ch.uzh.ifi.seal.soprafs19.rules.SimpleRuleSet;
import ch.uzh.ifi.seal.soprafs19.entity.Worker;

public class ApolloRuleSet extends SimpleRuleSet {

    // difference to parent class: can only not move on fields which are occupied with own worker
    @Override
    public Boolean checkMovePhase(Game before, Game after) {
        boolean isValid = false;

        Player currentPlayer = null;
        Worker w1 = null;
        Worker w2 = null;

        //gets current player
        for(Player p : before.getPlayers()){
            if(p.getIsCurrentPlayer()){
                currentPlayer = p;
            }
        }

        // gets the two worker of the current player
        for(Worker w : currentPlayer.getWorkers())
            if(w != null){
                if (w.getIsCurrentWorker()) {
                w1 = w;

            }   else {

                w2 = w;
            }}

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
                                //destination field has not your own worker
                                && (fieldAfterBackEnd.getWorker() != w2)
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

    @Override
    public Boolean checkWinCondition(Game game){
        // number of Workers of a Player, who can't move anymore
        int stuckWorkers = 0;

        // checking for both players
        for (Player player : game.getPlayers()) {

            // check positions of Workers
            for (Worker worker : player.getWorkers()) {

                // if worker has been at least dropped on the board
                if (worker.getField() != null) {

                    // win condition
                    if ((worker.getField().getBlocks() == 3 && !worker.getField().getHasDome())) {
                        // immediately return
                        // ...
                        return true;
                    }
                    // lose condition
                    else if (isWorkerStuck(game, worker)) {
                        ++stuckWorkers;
                    }
                }

            }

            // if both workers are stuck
            if (stuckWorkers == 2) {
                // ...
                return true;
            }

        }
        return false;

    }

    //Worker is not Stuck when a opponent worker is on a neighboring field
    private Boolean isWorkerStuck(Game game, Worker worker){

        int posX = worker.getField().getPosX();
        int posY = worker.getField().getPosY();

        Player currentPlayer = null;
        Worker w1 = null;
        Worker w2 = null;

        for(Player p : game.getPlayers()){
            if(p.getIsCurrentPlayer()){
                currentPlayer = p;
            }
        }

        for(Worker w : currentPlayer.getWorkers()){
            if(w != null){
                if(w.getIsCurrentWorker()){
                    w1 = w;

            }
                else{

                    w2 = w;
                }
        }}

        // finds neighbouring fields
        for (Field field : game.getBoard().getFields()) {
            if (field.getPosX() == posX + 1 || field.getPosX() == posX - 1 || field.getPosX() == posX) {
                if (field.getPosY() == posY + 1 || field.getPosY() == posY - 1 || field.getPosY() == posY) {
                    if (field.getPosX() != posX || field.getPosY() != posY) {
                        //Checks that it is possible to move to a neighbouring field
                        // it's free, if it has no dome, no worker,
                        if (!field.getHasDome() && (field.getBlocks()<= (worker.getField().getBlocks() + 1))) {
                            // if its the your own worker on a neighboring field, you are stuck
                            if(field.getWorker() == w2)
                                return false;
                            }
                        }

                    }
                }
            }


        return true;
    }

}


