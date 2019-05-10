package ch.uzh.ifi.seal.soprafs19.rules.godCards;

import ch.uzh.ifi.seal.soprafs19.entity.Field;
import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.entity.Player;
import ch.uzh.ifi.seal.soprafs19.entity.Worker;
import ch.uzh.ifi.seal.soprafs19.rules.SimpleRuleSet;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class AtlasRuleSet extends SimpleRuleSet {

    @Override
    protected Boolean isValidBuild() {
        // the field does not have a worker and no dome
        return (((fieldBuiltOnBackEnd.getWorker() == null) && !(fieldBuiltOnBackEnd.getHasDome())) &&
                // either the player added only a block
                ((fieldBuiltOnBackEnd.getBlocks() == fieldBuiltFrontEnd.getBlocks() - 1) && (fieldBuiltFrontEnd.getBlocks() <= 3)
                        && !fieldBuiltFrontEnd.getHasDome())
                ||
                // atlas can build a dome even on the ground
                (fieldBuiltFrontEnd.getHasDome()));
    }

}