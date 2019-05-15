package ch.uzh.ifi.seal.soprafs19.rules.godCards;

import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.rules.SimpleRuleSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class HephaestusRuleSet extends SimpleRuleSet {

    //your worker may build one additional block (not dome) on top of your first block

    @Override
    protected Boolean isValidBuild(){
        //the field does not have a worker and no dome
        return (((fieldBuiltOnBackEnd.getWorker() == null) && !(fieldBuiltOnBackEnd.getHasDome())) &&
                //either the player added only a block
                ((fieldBuiltOnBackEnd.getBlocks() == fieldBuiltFrontEnd.getBlocks() - 1) && (fieldBuiltFrontEnd.getBlocks() <= 3)
                    && !fieldBuiltFrontEnd.getHasDome())
                ||
                //or the player added a dome
                ((fieldBuiltOnBackEnd.getBlocks() == 3 && fieldBuiltFrontEnd.getHasDome()))
                ||
                //or the player adds 2 blocks, if there are less than 2 blocks already
                //TODO: check if this makes sense
                ((fieldBuiltOnBackEnd.getBlocks() == fieldBuiltFrontEnd.getBlocks() - 2) && (fieldBuiltFrontEnd.getBlocks() < 2)
                    && !fieldBuiltFrontEnd.getHasDome()));
    }

}
