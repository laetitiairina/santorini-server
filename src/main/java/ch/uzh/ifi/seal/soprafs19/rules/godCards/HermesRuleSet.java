package ch.uzh.ifi.seal.soprafs19.rules.godCards;

import ch.uzh.ifi.seal.soprafs19.entity.Field;
import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.rules.SimpleRuleSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class HermesRuleSet extends SimpleRuleSet {

    public Boolean isFieldFreeHermes(Game game, Field fieldBefore, Field fieldAfter, boolean isSecondMove) {
        for (Field field : game.getBoard().getFields()) {
            if (xBefore == field.getPosX() && yBefore == field.getPosY() && isValidMoveHermes(isSecondMove, fieldBefore, fieldAfter)) {
                return true;
            }
        }
        return false;
    }

    public Boolean isValidMoveHermes(boolean isSecondMove, Field fieldBefore, Field fieldAfter) {
        // origin field had a worker or it's the second move of a worker
        if ((fieldBefore.getWorker() != null) || isSecondMove
                // destination field is unoccupied
                && (fieldAfter.getWorker() == null)
                // destination field has no dome
                && (!fieldAfter.getHasDome())) {
            //check if blocks in after field are the same
            return (fieldAfter.getBlocks() == fieldBefore.getBlocks());
        }

        return false;
    }

    @Override
    public Boolean checkMovePhase(Game before, Game after) {

        //when player wants to move up or down with worker-> normal move
        if (setFieldsAfterMovePhase(after) && setFieldsBeforeMovePhase(before)) {
            if (fieldAfter.getBlocks() != fieldBefore.getBlocks()) {
                return isFieldFree(before, fieldBeforeBackEnd, fieldAfterBackEnd, false);

            }

            //special hermes Move-> on the same level, worker can move as much as he wants and can also stay on same place
            else
                return isFieldFreeHermes(before,fieldBeforeBackEnd,fieldAfterBackEnd,false);


        }
        return false;
    }
}
