package ch.uzh.ifi.seal.soprafs19.rules;

import ch.uzh.ifi.seal.soprafs19.entity.Game;

public class SimpleRuleSet implements IRuleSet {

    public Boolean checkMovePhase (Game before, Game after) {
        return false;
    }

    public Boolean checkBuildPhase(Game before, Game after) {
        return false;
    }

    public Boolean checkWinCondition(Game game) {
        return false;
    }

    public Boolean hasRuleForOpponentsTurn() {
        return false;
    }
}
