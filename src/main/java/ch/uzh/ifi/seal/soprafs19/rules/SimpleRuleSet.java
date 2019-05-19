package ch.uzh.ifi.seal.soprafs19.rules;

import ch.uzh.ifi.seal.soprafs19.entity.Field;
import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.entity.Player;
import ch.uzh.ifi.seal.soprafs19.entity.Worker;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Primary
@Component
@Transactional
public class SimpleRuleSet implements IRuleSet {

    // move
    protected Field fieldBefore = null;
    protected Field fieldAfter = null;

    protected Field fieldBeforeBackEnd = null;
    protected Field fieldAfterBackEnd = null;

    protected int xBefore = -1, yBefore = -1, xAfter = -1, yAfter = -1;

    // build
    /**
     *  Data Structure that maps the fields sent from the frontend (updated game) to the corresponding field on the backend (current state)
     */
    protected Map<Field, Field> frontendFieldToBackendField;

    protected int posWorkerX = -1, posWorkerY = -1;

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

    protected Boolean mapFrontendToBackendFields(Game before, Game after) {
        frontendFieldToBackendField = new HashMap<>();
        for (Field field : after.getBoard().getFields()) {
            if (field != null){
                if (field.getPosX() < 0 || field.getPosX() > 4 || field.getPosY() <0 || field.getPosY() > 4){
                    return false;
                }
                List<Field> backendFields = before.getBoard().getFields().stream().filter(backendField -> backendField.getId().compareTo(field.getId()) == 0).collect(Collectors.toList());
                if (backendFields.size() == 1){
                    frontendFieldToBackendField.put(field, backendFields.get(0));
                }
                else return false;
            }
        }
        return true;
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
            // check if it can move
            return isFieldFree(before, fieldBeforeBackEnd, fieldAfterBackEnd, false);
        }
        return false;
    }

    public Boolean isFieldFree(Game game, Field fieldBefore, Field fieldAfter, boolean isSecondMove) {
        for (Field field : neighbouringFields(game, fieldAfter.getPosX(), fieldAfter.getPosY())) {
            if (xBefore == field.getPosX() && yBefore == field.getPosY() && isValidMove(isSecondMove, fieldBefore, fieldAfter)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean checkMovePhaseOpponent(Game before, Game after) {
        return true;
    }

    public Boolean checkBuildPhase(Game before, Game after) {

        if (mapFrontendToBackendFields(before, after)) {
            setPosWorkerBuildPhase(before);

            // check if can build
            Boolean canBuild = frontendFieldToBackendField.keySet().stream().allMatch(f -> {
                return neighbouringFields(after, posWorkerX, posWorkerY).stream().anyMatch(n -> n.getPosX() == f.getPosX() && n.getPosY() == f.getPosY());
            });
            return canBuild && isValidBuild();
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

    protected Boolean isValidMove(boolean isSecondMove, Field fieldBefore, Field fieldAfter) {
        // origin field had a worker or it's the second move of a worker
        if ((fieldBefore.getWorker() != null) || isSecondMove
                // destination field is unoccupied
                && (fieldAfter.getWorker() == null)
                // destination field has no dome
                && (!fieldAfter.getHasDome())) {
            //check if blocks in after field is maximum 1 higher
            return (fieldAfter.getBlocks() <= fieldBefore.getBlocks() + 1);
        }
        return false;
    }

    protected Boolean isValidBuild() {
        return  getAmountOfBuildingFieldsCondition() && frontendFieldToBackendField.entrySet().stream().allMatch(entry -> {
           return (entry.getValue().getWorker() == null && !entry.getValue().getHasDome()) &&
                   (getAmountOfBuildingsPerFieldCondition(entry) && entry.getKey().getBlocks() <= 3 && !entry.getKey().getHasDome() ||
                   (entry.getValue().getBlocks() == 3 && entry.getKey().getHasDome() == true));
        });
    }

    /**
     *
     * @return Boolean Condition for the amount of Fields that can be built on in this phase
     */
    protected Boolean getAmountOfBuildingFieldsCondition() {
        return frontendFieldToBackendField.size() == 1;
    }

    /**
     *
     * @param entry: an entry in the hashmap frontendToBackendField
     *             entry.getKey() gets the key(frontend Field), and entry.getValue() gets the corresponding value (backend Field)
     * @return Boolean condition for the amount of blocks that can be built on each field
     */
    protected Boolean getAmountOfBuildingsPerFieldCondition(Map.Entry<Field, Field> entry) {
        return (entry.getValue().getBlocks() == entry.getKey().getBlocks() - 1);
    }

}
