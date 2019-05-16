package ch.uzh.ifi.seal.soprafs19.rules;

import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.entity.Player;
import ch.uzh.ifi.seal.soprafs19.entity.Worker;

public interface IRuleSet {

    Boolean checkMovePhase(Game before, Game after);
    Boolean checkBuildPhase(Game before, Game after);
    Player checkWinCondition(Game game);
    Boolean isWorkerStuck(Game game, Worker worker);
    Boolean checkMovePhaseOpponent(Game before, Game after);

}