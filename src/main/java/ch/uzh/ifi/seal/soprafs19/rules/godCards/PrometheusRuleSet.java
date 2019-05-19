package ch.uzh.ifi.seal.soprafs19.rules.godCards;

import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.entity.Worker;
import ch.uzh.ifi.seal.soprafs19.rules.SimpleRuleSet;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ch.uzh.ifi.seal.soprafs19.entity.Field;

import java.util.ArrayList;
import java.util.List;

@Component
@Transactional
public class PrometheusRuleSet extends SimpleRuleSet {

    @Override
    public Boolean checkMovePhase(Game before, Game after) {
        if (after.getBoard().getFields().size() == 2) {
            return super.checkMovePhase(before, after);
        }
        else if(after.getBoard().getFields().size() == 3) {
            neighbouringFields(before, after.getBoard().getFields().get(0).getPosX(), after.getBoard().getFields().get(0).getPosY()).forEach(field -> {
                if (field.getWorker() != null && field.getWorker().getPlayer() == before.getCurrentPlayer()){
                    before.getBoard().getFieldByCoordinates(field.getPosX(), field.getPosY()).getWorker().setIsCurrentWorker(true);
                }
            });
            Game cloneAfter = SerializationUtils.clone(after);
            List<Field> fields = new ArrayList<>();
            fields.add(after.getBoard().getFields().get(0));
            cloneAfter.getBoard().setFields(fields);
            if (!checkBuildPhase(before, cloneAfter)) {
                before.getCurrentPlayer().getWorkers().forEach(worker -> worker.setIsCurrentWorker(false));
                return false;
            }
            before.getCurrentPlayer().getWorkers().forEach(worker -> worker.setIsCurrentWorker(false));
            return isValidMove(false, after.getBoard().getFields().get(1), after.getBoard().getFields().get(2)) &&
                    after.getBoard().getFields().get(1).getBlocks() == after.getBoard().getFields().get(2).getBlocks();

        }
        return false;
    }

}