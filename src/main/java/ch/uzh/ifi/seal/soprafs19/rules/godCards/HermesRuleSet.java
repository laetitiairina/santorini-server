package ch.uzh.ifi.seal.soprafs19.rules.godCards;

import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.rules.SimpleRuleSet;
import ch.uzh.ifi.seal.soprafs19.entity.Field; 
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@Transactional
public class HermesRuleSet extends SimpleRuleSet {

    //Rekursiv gemäss laeti
    public boolean hermesPath(Game before, int blockLevel, long destinationId, Field field) {
        for (Field neighbour : neighbouringFields(before, field.getPosX(), field.getPosY())) {
            if (neighbour.getBlocks() == blockLevel) {
                if (neighbour.getId() == destinationId) {
                    return true;
                } else {
                    return hermesPath(before, blockLevel, destinationId, neighbour);
                }
            }
        }
        return false;
    }

    @Override
    public Boolean checkMovePhase(Game before, Game after) {

        //when player wants to move up or down with worker-> normal move
        if (setFieldsAfterMovePhase(before) && setFieldsBeforeMovePhase(after)) {
            if (fieldAfter.getBlocks() != fieldBefore.getBlocks()) {
                return isFieldFree(before, fieldBeforeBackEnd, fieldAfterBackEnd, false);

            }

            //special hermes Move-> on the same level, worker can move as much as he wants and can also stay on same place
            else return hermesPath(before, fieldBefore.getBlocks(), fieldAfter.getId(), fieldBefore);
        }

        return false;
    }
}
