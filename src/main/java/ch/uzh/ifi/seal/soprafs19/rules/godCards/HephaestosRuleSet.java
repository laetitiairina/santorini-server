package ch.uzh.ifi.seal.soprafs19.rules.godCards;

import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.rules.SimpleRuleSet;

public class HephaestosRuleSet extends SimpleRuleSet {

    @Override
    public Boolean checkBuildPhase(Game before, Game after) {
        return false;
    }

}
