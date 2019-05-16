package ch.uzh.ifi.seal.soprafs19.rules;

import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.entity.Player;
import ch.uzh.ifi.seal.soprafs19.entity.Worker;

public interface IRuleSet {

    public Boolean checkMovePhase (Game before, Game after);
    public Boolean checkBuildPhase(Game before, Game after);
    public Player checkWinCondition(Game game);
    public Boolean isWorkerStuck(Game game, Worker worker);
    public Boolean checkMovePhaseOpponent(Game before, Game after);

}