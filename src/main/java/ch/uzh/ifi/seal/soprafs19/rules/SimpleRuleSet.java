package ch.uzh.ifi.seal.soprafs19.rules;

import ch.uzh.ifi.seal.soprafs19.entity.Field;
import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.entity.Player;
import ch.uzh.ifi.seal.soprafs19.entity.Worker;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Primary
@Component
@Transactional
public class SimpleRuleSet implements IRuleSet {

    // move
    protected Field fieldBefore = null;
    protected Field fieldAfter = null;

    protected Field fieldBeforeBackEnd = null;
    protected Field fieldAfterBackEnd = null;

    protected int blockBefore = -1;
    protected int blockAfter = -1;

    protected int xBefore = -1, yBefore = -1, xAfter = -1, yAfter = -1;

    // build
    protected Field fieldBuiltFrontEnd = null;
    protected Field fieldBuiltOnBackEnd = null;

    protected int posWorkerX = -1, posWorkerY = -1;
    protected int posBuiltFieldX = -1, posBuiltFieldY = -1;

    protected Boolean setFieldsBeforeMovePhase(Game before) {
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
        return !(fieldBeforeBackEnd == null || fieldAfterBackEnd == null);
    }

    protected Boolean setFieldsAfterMovePhase(Game after) {
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
        // faulty information by front-end check
        return !(fieldAfter == null || fieldBefore == null);
    }

    protected Boolean setFieldBeforeBuildPhase(Game before) {
        for (Field field : before.getBoard().getFields()) {
            if (field.getPosX() == posBuiltFieldX && field.getPosY() == posBuiltFieldY) {
                fieldBuiltOnBackEnd = field;
            }
        }

        // faulty information by front-end
        return (fieldBuiltOnBackEnd != null);
    }

    protected Boolean setFieldAfterBuildPhase(Game after) {
        //adds the position of the Field that has been sent from the front-end
        for (Field field : after.getBoard().getFields()) {
            if (field != null) {
                fieldBuiltFrontEnd = field;
                posBuiltFieldX = fieldBuiltFrontEnd.getPosX();
                posBuiltFieldY = fieldBuiltFrontEnd.getPosY();
            }
        }

        // if front-end sent no field at all, or faulty one
        return !(fieldBuiltFrontEnd == null
                || (posBuiltFieldX < 0) || (posBuiltFieldX > 4) || (posBuiltFieldY < 0) || (posBuiltFieldY > 4));
    }

    protected void setPosWorkerBuildPhase(Game before) {
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
    }

    public Boolean checkMovePhase(Game before, Game after) {

        if (setFieldsAfterMovePhase(after) && setFieldsBeforeMovePhase(before)) {
            blockBefore = fieldBefore.getBlocks();
            blockAfter = fieldAfter.getBlocks();

            // check if it can move
            for (Field field : neighbouringFields(before, fieldAfter.getPosX(), fieldAfter.getPosY())) {
                if (xBefore == field.getPosX() && yBefore == field.getPosY() && isValidMove(1)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Boolean checkBuildPhase(Game before, Game after) {

        if (setFieldAfterBuildPhase(after) && setFieldBeforeBuildPhase(before)) {
            setPosWorkerBuildPhase(before);

            // check if can build
            for (Field field : neighbouringFields(after, posWorkerX, posWorkerY)) {
                if (posBuiltFieldX ==field.getPosX() && posBuiltFieldY == field.getPosY() && isValidBuild()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * returns winning player or null
     *
     * @param game
     * @return
     */
    public Player checkWinCondition(Game game) {

        // checking for both players
        for (Player player : game.getPlayers()) {

            // number of Workers of a Player, who can't move anymore
            int stuckWorkers = 0;

            // check positions of Workers
            for (Worker worker : player.getWorkers()) {

                // if worker has been at least dropped on the board
                if (worker.getField() != null) {

                    // win condition
                    if (hasWon(worker)) {
                        // immediately return
                        return player;
                    }
                    // lose condition
                    else if (isWorkerStuck(game, worker)) {
                        stuckWorkers++;
                    }
                }
            }

            // if both workers are stuck
            if (stuckWorkers == 2) {
                for (Player p : game.getPlayers()) {
                    if (!player.getId().equals(p.getId())) {
                        return p;
                    }
                }
            }

        }
        return null;
    }


    public Boolean hasRuleForOpponentsTurn() {
        return false;
    }

    protected Boolean hasWon(Worker worker) {
        return (worker.getField().getBlocks() == 3 && !worker.getField().getHasDome());
    }

    protected List<Field> neighbouringFields(Game game, int posX, int posY) {
        List<Field> fields = new ArrayList<>();

        // finds neighbouring fields
        for (Field field : game.getBoard().getFields()) {
            // on x axis
            if (field.getPosX() == posX - 1 || field.getPosX() == posX || field.getPosX() == posX + 1) {
                // on y axis
                if (field.getPosY() == posY - 1 || field.getPosY() == posY || field.getPosY() == posY + 1) {
                    // not the same field
                    if (field.getPosX() != posX || field.getPosY() != posY) {
                        fields.add(field);
                    }
                }
            }
        }
        return fields;
    }

    /**
     * Method that Checks if a Worker can not move anymore
     *
     * @param game
     * @param worker
     * @return
     */
    public Boolean isWorkerStuck(Game game, Worker worker) {
        for (Field field : neighbouringFields(game, worker.getField().getPosX(), worker.getField().getPosY())) {
            // it's free, if it has no dome, no worker
            if (!field.getHasDome() && field.getWorker() == null) {

                // or max. one block more than the worker's current field
                if ((field.getBlocks() - 1) <= (worker.getField().getBlocks())) {
                    // a field is free
                    return false;
                }
            }
        }
        return true;
    }

    protected Boolean isValidMove(int difference) {
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
                return (blockAfter <= blockBefore + difference);
            }
        }
        return false;
    }

    protected Boolean isValidBuild() {
        // the field does not have a worker and no dome
        return  (((fieldBuiltOnBackEnd.getWorker() == null) && !(fieldBuiltOnBackEnd.getHasDome())) &&
                // either the player added only a block
                ((fieldBuiltOnBackEnd.getBlocks() == fieldBuiltFrontEnd.getBlocks() - 1) && (fieldBuiltFrontEnd.getBlocks() <= 3)
                        && !fieldBuiltFrontEnd.getHasDome())
                ||
                // or the player added a dome
                ((fieldBuiltOnBackEnd.getBlocks() == 3) && fieldBuiltFrontEnd.getHasDome()));
    }
}
