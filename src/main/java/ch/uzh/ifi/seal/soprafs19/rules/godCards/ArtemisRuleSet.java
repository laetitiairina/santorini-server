package ch.uzh.ifi.seal.soprafs19.rules.godCards;

import ch.uzh.ifi.seal.soprafs19.entity.Field;
import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.rules.SimpleRuleSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class ArtemisRuleSet extends SimpleRuleSet {

    @Override
    public Boolean checkMovePhase(Game before, Game after) {

        if (setFieldsAfterMovePhase(after) && setFieldsBeforeMovePhase(before)) {
            // check if it can move
            for (Field field : neighbouringFields(before, fieldBefore.getPosX(), fieldBefore.getPosY())) {
                // first move
                if (isValidMove(false, fieldBeforeBackEnd, field)) {
                    if (xAfter == field.getPosX() && yAfter == field.getPosY()) {
                        return true;
                    }
                    // it was valid to step on first field, check for second field
                    else {
                        // second move
                        for (Field field2 : neighbouringFields(before, field.getPosX(), field.getPosY())) {
                            if (xAfter == field2.getPosX() && yAfter == field2.getPosY() && isValidMove(true, field, fieldAfterBackEnd)
                                    // not back to same field
                                    && !(field2.getPosX().equals(fieldBefore.getPosX()) && field2.getPosY().equals(fieldBefore.getPosY()))) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}

