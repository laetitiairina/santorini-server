package ch.uzh.ifi.seal.soprafs19.rules.godCards;

import ch.uzh.ifi.seal.soprafs19.rules.SimpleRuleSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class AtlasRuleSet extends SimpleRuleSet {

    @Override
    protected Boolean isValidBuild() {
        // the field does not have a worker and no dome
        return frontendFieldToBackendField.entrySet().stream().allMatch(entry -> {
            return (entry.getValue().getWorker() == null || !entry.getValue().getHasDome()) &&
                    ((entry.getValue().getBlocks() == entry.getKey().getBlocks() - 1) && entry.getKey().getBlocks() <= 3 && !entry.getKey().getHasDome() ||
                            (entry.getValue().getBlocks() <= 3 && entry.getKey().getHasDome() == true));
        });
    }

}