package ch.uzh.ifi.seal.soprafs19.rules.godCards;

import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.rules.SimpleRuleSet;

public class PanRuleSet extends SimpleRuleSet {

    @Override
    public Boolean checkWinCondition(Game before, Game after) {
        return false;
    }

}