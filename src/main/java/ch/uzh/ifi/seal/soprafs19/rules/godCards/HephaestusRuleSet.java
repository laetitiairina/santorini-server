package ch.uzh.ifi.seal.soprafs19.rules.godCards;

import ch.uzh.ifi.seal.soprafs19.entity.Field;
import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.rules.SimpleRuleSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
@Transactional
public class HephaestusRuleSet extends SimpleRuleSet {

    @Override
    protected Boolean getAmountOfBuildingsPerFieldCondition(Map.Entry<Field, Field> entry) {
        return super.getAmountOfBuildingsPerFieldCondition(entry) || entry.getValue().getBlocks() == entry.getKey().getBlocks() - 2;
    }
}
 