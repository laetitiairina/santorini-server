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
            mapFrontendToBackendFields(before, after);
            List<Field> updatedFields = after.getBoard().getFields();
            List<Field> dummyUpdatedFields = new ArrayList<>();
            neighbouringFields(before, frontendFieldToBackendField.get(after.getBoard().getFields().get(0)).getPosX(), frontendFieldToBackendField.get(after.getBoard().getFields().get(0)).getPosY()).forEach(field -> {
                if (field.getWorker() != null && field.getWorker().getPlayer() == before.getCurrentPlayer()){
                    field.getWorker().setIsCurrentWorker(true);
                }
            });
            dummyUpdatedFields.add(updatedFields.get(0));
            after.getBoard().setFields(dummyUpdatedFields);
            if (!checkBuildPhase(before, after)) {
                before.getCurrentPlayer().getWorkers().forEach(worker -> worker.setIsCurrentWorker(false));
                return false;
            }

            before.getCurrentPlayer().getWorkers().forEach(worker -> worker.setIsCurrentWorker(false));
            dummyUpdatedFields.clear();
            dummyUpdatedFields.add(updatedFields.get(1));
            dummyUpdatedFields.add(updatedFields.get(2));
            after.getBoard().setFields(dummyUpdatedFields);
            Boolean isValidMovePhase = super.checkMovePhase(before, after) &&
                    updatedFields.get(1).getBlocks() == updatedFields.get(2).getBlocks();
            //restore after game state
            after.getBoard().setFields(updatedFields);
            return isValidMovePhase;
        }
        return false;
    }

}