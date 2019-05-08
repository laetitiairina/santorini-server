package ch.uzh.ifi.seal.soprafs19.rules.godCards;

import ch.uzh.ifi.seal.soprafs19.entity.Field;
import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.entity.Player;
import ch.uzh.ifi.seal.soprafs19.entity.Worker;
import ch.uzh.ifi.seal.soprafs19.rules.SimpleRuleSet;

public class AtlasRuleSet extends SimpleRuleSet {

    //same as parent class but, you can built a dome on any level -> player added a dome line changed
    @Override
    public Boolean checkBuildPhase(Game before, Game after) {

        boolean isValid = false;

        Field fieldAfterBuilt = null;
        Field fieldBuiltOnBackEnd = null;

        int posWorkerX = -1, posWorkerY = -1;
        int posBuiltFieldX = -1, posBuiltFieldY = -1;

        //adds the position of the Field that has been sent from the front-end
        for (Field field : after.getBoard().getFields()) {
            if (field != null) {
                fieldAfterBuilt = field;
                posBuiltFieldX = fieldAfterBuilt.getPosX();
                posBuiltFieldY = fieldAfterBuilt.getPosY();
            }
        }

        // if front-end sent no field at all, or faulty one
        if (fieldAfterBuilt == null
                || (posBuiltFieldX < 0) || (posBuiltFieldX > 4) || (posBuiltFieldY < 0)|| (posBuiltFieldY > 4)) {
            return false;
        }

        //gets the Position of the current Worker which is building a block
        for (Player p : before.getPlayers()) {
            if (p.getIsCurrentPlayer()) {
                for (Worker w : p.getWorkers()) {
                    if (w.getIsCurrentWorker()) {
                        posWorkerX = w.getField().getPosX();
                        posWorkerY = w.getField().getPosY();
                    }
                }
            }
        }

        for (Field field : before.getBoard().getFields()) {
            if (field.getPosX() == posBuiltFieldX && field.getPosY() == posBuiltFieldY) {
                fieldBuiltOnBackEnd = field;
            }
        }

        // faulty information by front-end
        if (fieldBuiltOnBackEnd == null) {
            return false;
        }

        // Built on 1 or 0 fields next to the worker on X-axis
        if ((posBuiltFieldX == posWorkerX) || (posBuiltFieldX == posWorkerX - 1) || (posBuiltFieldX == posWorkerX + 1)) {

            // Built on 1 or 0 fields next to the worker on Y-axis
            if ((posBuiltFieldY == posWorkerY) || (posBuiltFieldY == posWorkerY - 1) || (posBuiltFieldY == posWorkerY + 1)) {

                // can not built on the same field as the worker
                if ((posBuiltFieldX != posWorkerX) || (posBuiltFieldY != posWorkerY)) {

                    // the field does not have a worker and no dome
                    if (((fieldBuiltOnBackEnd.getWorker() == null) && !(fieldBuiltOnBackEnd.getHasDome())) &&
                            // either the player added only a block
                            ((fieldBuiltOnBackEnd.getBlocks() == fieldAfterBuilt.getBlocks() - 1) && (fieldAfterBuilt.getBlocks() <= 3)
                                    && !fieldAfterBuilt.getHasDome())
                            ||
                            // atlas can build a dome even on the ground
                            (fieldAfterBuilt.getHasDome())) {
                        isValid = true;
                    }
                }
            }
        }

        return isValid;

    }

}