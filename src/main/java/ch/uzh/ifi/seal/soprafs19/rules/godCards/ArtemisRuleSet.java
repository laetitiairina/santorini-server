package ch.uzh.ifi.seal.soprafs19.rules.godCards;

import ch.uzh.ifi.seal.soprafs19.entity.Field;
import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.rules.SimpleRuleSet;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@Transactional
public class ArtemisRuleSet extends SimpleRuleSet {

    @Override
    public Boolean checkMovePhase(Game before, Game after) {

        if (setFieldsAfterMovePhase(after) && setFieldsBeforeMovePhase(before)) {
            blockBefore = fieldBefore.getBlocks();
            blockAfter = fieldAfter.getBlocks();

            // check if it can move
            for (Field field : neighbouringFields(before, fieldAfter.getPosX(), fieldAfter.getPosY())) {
                // normal move
                if (xBefore == field.getPosX() && yBefore == field.getPosY() && isValidMove(1)) {
                    return true;
                } else {
                    // two fields
                    for (Field field2 : neighbouringFields(before, field.getPosX(), field.getPosY())) {
                        if (xBefore == field2.getPosX() && yBefore == field2.getPosY() && isValidMove(2)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
