package ch.uzh.ifi.seal.soprafs19.rules.godCards;

import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.rules.SimpleRuleSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class AthenaRuleSet extends SimpleRuleSet {

    @Override
    public Boolean checkMovePhase(Game before, Game after) {
        return false;
    }

    @Override
    public Boolean hasRuleForOpponentsTurn () {
        return true;
    }

}
