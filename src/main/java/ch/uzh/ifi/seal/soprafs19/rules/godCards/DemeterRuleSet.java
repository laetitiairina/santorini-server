package ch.uzh.ifi.seal.soprafs19.rules.godCards;

import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.rules.SimpleRuleSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class DemeterRuleSet extends SimpleRuleSet {

    @Override
    protected Boolean isValidBuild() {
        Boolean isValid = super.isValidBuild();
        // TODO check if when second build phase not same field
        return isValid;
    }

}
