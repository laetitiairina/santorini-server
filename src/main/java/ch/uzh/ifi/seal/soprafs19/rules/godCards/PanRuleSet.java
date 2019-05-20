package ch.uzh.ifi.seal.soprafs19.rules.godCards;

import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.entity.Player;
import ch.uzh.ifi.seal.soprafs19.rules.SimpleRuleSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class PanRuleSet extends SimpleRuleSet {

    @Override
    public Player checkWinCondition(Game game) {
        Player winner = super.checkWinCondition(game);
        if (winner != null) {
            return winner;
        }
        else if(game.getBlockDifference() <= -2) {
            return game.getCurrentPlayer();
        }
        return null;
    }

}