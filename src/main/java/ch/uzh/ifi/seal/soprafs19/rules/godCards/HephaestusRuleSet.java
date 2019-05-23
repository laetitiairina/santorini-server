package ch.uzh.ifi.seal.soprafs19.rules.godCards;

import ch.uzh.ifi.seal.soprafs19.entity.Field;
import ch.uzh.ifi.seal.soprafs19.rules.SimpleRuleSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

@Component
@Transactional
public class HephaestusRuleSet extends SimpleRuleSet {

    @Override
    protected Boolean getAmountOfBuildingFieldsCondition() {
        return super.getAmountOfBuildingFieldsCondition() || frontendFieldToBackendField.keySet().stream().map(f -> f.getId()).collect(Collectors.toSet()).size() == 1;
    }

    @Override
    protected Boolean getAmountOfBuildingsPerFieldCondition(Map.Entry<Field, Field> entry) {
        return super.getAmountOfBuildingsPerFieldCondition(entry) || entry.getValue().getBlocks() == entry.getKey().getBlocks() - 2;
    }
}
 