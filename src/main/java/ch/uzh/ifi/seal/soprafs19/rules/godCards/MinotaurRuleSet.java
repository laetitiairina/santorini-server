package ch.uzh.ifi.seal.soprafs19.rules.godCards;

import ch.uzh.ifi.seal.soprafs19.entity.Field;
import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.entity.Player;
import ch.uzh.ifi.seal.soprafs19.rules.SimpleRuleSet;
import ch.uzh.ifi.seal.soprafs19.entity.Worker;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@Transactional
public class MinotaurRuleSet extends SimpleRuleSet {

    //extra
    private Worker ownWorker;

    //can only not move on fields, which are occupied with own worker
    //following method may be duplicate from ApolloRuleSet.java
    @Override
    public Boolean checkMovePhase(Game before, Game after) {

        if(setFieldsBeforeMovePhase(before) && setFieldsAfterMovePhase(after)){
            //get Worker of current player which is not being moved
            for (Player p : before.getPlayers()) {
                if (p.getIsCurrentPlayer()) {
                    for (Worker w : p.getWorkers()) {
                        //
                        if (w != null && !fieldBeforeBackEnd.getWorker().getId().equals(w.getId())) {
                            ownWorker = w;
                        }
                    }
                }
            }

            //faulty info from front-end
            if(ownWorker == null){
                return false;
            }

            //check if worker can move
            return isFieldFree(before, fieldBeforeBackEnd, fieldAfterBackEnd, false);
        }
        return false;

        //Worker is not stuck when a opponent worker is on a neighboring field
        //in progress
        @Override
        public Boolean isWorkerStuck(Game game, Worker worker){
            return true;
        }
    }



}
