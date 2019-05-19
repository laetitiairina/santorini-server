package ch.uzh.ifi.seal.soprafs19.rules.godCards;

import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.rules.SimpleRuleSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class AthenaRuleSet extends SimpleRuleSet {

    @Override
    public Boolean checkMovePhaseOpponent(Game before, Game after) {
        Boolean isValidMove = true;
        if (before.getBlockDifference() > 0) {
            checkMovePhase(before, after);
            isValidMove = fieldAfter.getBlocks() - fieldBefore.getBlocks() <= 0;
        }
        return isValidMove;
    }



}
