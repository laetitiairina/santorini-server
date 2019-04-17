package ch.uzh.ifi.seal.soprafs19.rules;

import ch.uzh.ifi.seal.soprafs19.entity.Game;

public interface IRuleSet {

    public Boolean checkMovePhase (Game before, Game after);
    public Boolean checkBuildPhase(Game before, Game after);
    public Boolean checkWinCondition(Game before, Game after);
    public Boolean hasRuleForOpponentsTurn();

}