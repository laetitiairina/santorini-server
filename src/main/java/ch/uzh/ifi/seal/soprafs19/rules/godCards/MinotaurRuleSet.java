package ch.uzh.ifi.seal.soprafs19.rules.godCards;

import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.rules.SimpleRuleSet;

public class MinotaurRuleSet extends SimpleRuleSet {

    @Override
    public Boolean checkMovePhase(Game before, Game after) {
        return false;
    }

}
