package ch.uzh.ifi.seal.soprafs19.rules.godCards;

import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.entity.Worker;
import ch.uzh.ifi.seal.soprafs19.rules.SimpleRuleSet;
import ch.uzh.ifi.seal.soprafs19.entity.Field; 
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Component
@Transactional
public class HermesRuleSet extends SimpleRuleSet {

    private Game gameBefore;

    private List<Field> blockedFields;
    private List<Field> path;

    @Override
    public Boolean checkMovePhase(Game before, Game after) {
        List<Field> frontEndFields = after.getBoard().getFields();
        gameBefore = before;

        if (!mapFrontendToBackendFields(before, after)) {
            return false;
        }
        //check if no player moved
        if ((frontEndFields.get(0).getId() == frontEndFields.get(1).getId() &&
                frontEndFields.get(2).getId() == frontEndFields.get(3).getId()) &&
                frontEndFields.stream().allMatch(field -> field.getWorker() != null)){
            return true;
        }
        // check if fields are sent in correct order
        if (!(frontendFieldToBackendField.get(frontEndFields.get(0)).getWorker().equals(frontEndFields.get(1).getWorker()) &&
            frontendFieldToBackendField.get(frontEndFields.get(2)).getWorker().equals(frontEndFields.get(3).getWorker()))) {
            return false;
        }
        blockedFields = new ArrayList<>();
        path = new ArrayList<>();
        path.add(frontendFieldToBackendField.get(frontEndFields.get(0)));
        if (!isValidMove(false, frontendFieldToBackendField.get(frontEndFields.get(0)), frontendFieldToBackendField.get(frontEndFields.get(1)))){
            return false;
        }
        blockedFields.clear();
        path.clear();
        path.add(frontendFieldToBackendField.get(frontEndFields.get(2)));
        if (!isValidMove(false, frontendFieldToBackendField.get(frontEndFields.get(2)), frontendFieldToBackendField.get(frontEndFields.get(3)))) {
            return false;
        }
        // if one worker moves up or down only one can move
        if(Math.abs(frontEndFields.get(1).getBlocks() - frontEndFields.get(0).getBlocks()) == 1 && !frontEndFields.get(2).equals(frontEndFields.get(3))) {
            return false;
        }
        if (Math.abs(frontEndFields.get(3).getBlocks() - frontEndFields.get(2).getBlocks()) == 1 && !frontEndFields.get(0).equals(frontEndFields.get(1))){
            return false;
        }
        return true;
    }

    @Override
    protected Boolean isValidMove(boolean isSecondMove, Field fieldBefore, Field fieldAfter) {

        //if worker did not move is valid
        if (fieldBefore.equals(fieldAfter))
        {return true;}

        // origin field had a worker or it's the second move of a worker
        else if (((fieldBefore.getWorker() != null) || isSecondMove)
                // destination field is unoccupied
                && (fieldAfter.getWorker() == null)
                // destination field has no dome
                && (!fieldAfter.getHasDome())) {
            //check if blocks in after field is maximum 1 higher if worker only moves one field
            if (isNeighbouringField(gameBefore, fieldBefore, fieldAfter)) {
                return (fieldAfter.getBlocks() <= fieldBefore.getBlocks() + 1);
            }
            else return hasValidPath(fieldBefore, fieldAfter);
        }
        return false;
    }

    private Boolean isNeighbouringField(Game game, Field fieldBefore, Field fieldAfter) {
        return neighbouringFields(game, fieldBefore.getPosX(), fieldBefore.getPosY()).stream().anyMatch(field -> field.equals(fieldAfter));
    }

    private Boolean hasValidPath(Field origin, Field target) {
        int height = origin.getBlocks();
        List<Field> temp;
        List<Field> tempPossible = new ArrayList<>();

        temp = neighbouringFields(gameBefore, origin.getPosX(), origin.getPosY()).stream().filter(x -> !blockedFields.contains(x) && !path.contains(x)).collect(Collectors.toList());

        for (Field field : temp) {
            Boolean possible = fieldIsPossible(field, height);
            if (possible) {
                if(field.getId().equals(target.getId())) {
                    return true;
                }
                else {
                    tempPossible.add(field);
                }
            }
        }

        // if no more possible path from this node
        if (tempPossible.isEmpty()) {
            blockedFields.add(origin);
        }
        if (!path.get(path.size()-1).equals(origin)){
            path.add(origin);
        }

        Boolean isValid = false;

        for (Field field : tempPossible) {
            isValid = hasValidPath(field, target);
            if (isValid) {
                break;
            }
        }
        if (isValid == true)
            return true;

        if (path.size() > 0) {
            path.remove(path.size() -1);
        }
        return false;
    }

    private Boolean fieldIsPossible(Field field, int height) {
        if(field.getBlocks() != height || field.getWorker() != null || field.getHasDome() == true) {
            blockedFields.add(field);
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    public Boolean checkBuildPhase(Game before, Game after) {
        if (mapFrontendToBackendFields(before, after)) {
            List<Worker> workers = before.getCurrentPlayer().getWorkers();

            List<Field> neighbouringFields = neighbouringFields(before, workers.get(0).getField().getPosX(), workers.get(0).getField().getPosY());
            if (!neighbouringFields.addAll(neighbouringFields(before, workers.get(1).getField().getPosX(), workers.get(1).getField().getPosY()))) {
                return false;
            }

            // check if can build
            Boolean canBuild = frontendFieldToBackendField.keySet().stream().allMatch(f -> {
                return  neighbouringFields.stream().anyMatch(n -> n.equals(f));
            });
            return canBuild && isValidBuild();
        }
        return false;
    }
}
