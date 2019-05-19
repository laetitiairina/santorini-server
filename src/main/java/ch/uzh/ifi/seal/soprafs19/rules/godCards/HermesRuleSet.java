package ch.uzh.ifi.seal.soprafs19.rules.godCards;

import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.rules.SimpleRuleSet;
import ch.uzh.ifi.seal.soprafs19.entity.Field; 
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;


@Component
@Transactional
public class HermesRuleSet extends SimpleRuleSet {

    private Game gameBefore;

    @Override
    public Boolean checkMovePhase(Game before, Game after) {
        //TODO implement with helper methods and refactorings of other branch
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
        if (!isValidMove(false, frontendFieldToBackendField.get(frontEndFields.get(0)), frontendFieldToBackendField.get(frontEndFields.get(1)))){
            return false;
        }
        if (!isValidMove(false, frontendFieldToBackendField.get(frontEndFields.get(2)), frontendFieldToBackendField.get(frontEndFields.get(3)))) {
            return false;
        }
        return true;
    }

    @Override
    protected Boolean isValidMove(boolean isSecondMove, Field fieldBefore, Field fieldAfter) {

        // origin field had a worker or it's the second move of a worker
        if ((fieldBefore.getWorker() != null) || isSecondMove
                // destination field is unoccupied
                && ((fieldAfter.getWorker() == null && !fieldBefore.equals(fieldAfter)) ||
                (fieldBefore.equals(fieldAfter)))
                // destination field has no dome
                && (!fieldAfter.getHasDome())) {
            //check if blocks in after field is maximum 1 higher if woker only moves one field
            if (isNeighbouringField(gameBefore, fieldAfter)) {
                return (fieldAfter.getBlocks() <= fieldBefore.getBlocks() + 1);
            }
            else return hasValidPath(fieldBefore, fieldAfter) && fieldAfter.getBlocks() == fieldBefore.getBlocks();
        }
        return false;
    }

    private Boolean isNeighbouringField(Game game, Field fieldAfter) {
        return neighbouringFields(game, fieldAfter.getPosX(), fieldAfter.getPosY()).stream().anyMatch(field -> field.equals(fieldAfter));
    }

    private Boolean hasValidPath(Field origin, Field target) {
        // TODO replace this with dynamic programming solution because of stack overflow error
        /*int blockLevel = origin.getBlocks();
        for (Field neighbour : neighbouringFields(gameBefore, origin.getPosX(), origin.getPosY())) {
            if (neighbour.getBlocks() == blockLevel) {
                if (neighbour.equals(target)){
                    return true;
                }
                else {
                    return hasValidPath(neighbour, target);
                }
            }
        }
        return false;*/
        return true;
    }
}
