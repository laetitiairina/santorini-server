package ch.uzh.ifi.seal.soprafs19.rules.godCards;

import ch.uzh.ifi.seal.soprafs19.rules.SimpleRuleSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class DemeterRuleSet extends SimpleRuleSet {

    @Override
    protected Boolean getAmountOfBuildingFieldsCondition() {
        return frontendFieldToBackendField.size() <= 2 && frontendFieldToBackendField.size() > 0;
    }
}
