package ch.uzh.ifi.seal.soprafs19.rules;

import ch.uzh.ifi.seal.soprafs19.entity.Player;
import ch.uzh.ifi.seal.soprafs19.rules.godCards.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Primary
@Component
@Transactional
public class RuleFactory {

    @Autowired
    private SimpleRuleSet simpleRuleSet;

    @Autowired
    private ApolloRuleSet apolloRuleSet;

    @Autowired
    private ArtemisRuleSet artemisRuleSet;

    @Autowired
    private AthenaRuleSet athenaRuleSet;

    @Autowired
    private AtlasRuleSet atlasRuleSet;

    @Autowired
    private DemeterRuleSet demeterRuleSet;

    @Autowired
    private HephaestusRuleSet hephaestusRuleSet;

    @Autowired
    private HermesRuleSet hermesRuleSet;

    @Autowired
    private MinotaurRuleSet minotaurRuleSet;

    @Autowired
    private PanRuleSet panRuleSet;

    @Autowired
    private PrometheusRuleSet prometheusRuleSet;

    public IRuleSet getRuleSet(Player player) {
        if (player.getCard() != null) {
            switch (player.getCard().toString()) {
                case ("APOLLO"):
                    return apolloRuleSet;
                case("ARTEMIS"):
                    return artemisRuleSet;
                case("ATHENA"):
                    return athenaRuleSet;
                case("ATLAS"):
                    return atlasRuleSet;
                case("DEMETER"):
                    return demeterRuleSet;
                case("HEPHAESTUS"):
                    return hephaestusRuleSet;
                case("HERMES"):
                    return hermesRuleSet;
                case("MINOTAUR"):
                    return minotaurRuleSet;
                case("PAN"):
                    return panRuleSet;
                case("PROMETHEUR"):
                    return prometheusRuleSet;
            }
        }
        return simpleRuleSet;
    }

}
