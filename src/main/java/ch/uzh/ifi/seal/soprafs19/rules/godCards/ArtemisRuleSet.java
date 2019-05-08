package ch.uzh.ifi.seal.soprafs19.rules.godCards;

import ch.uzh.ifi.seal.soprafs19.entity.Field;
import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.rules.SimpleRuleSet;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Component
@Transactional
public class ArtemisRuleSet extends SimpleRuleSet {

    @Override
    public Boolean checkMovePhase(Game before, Game after) {

        boolean isValid = false;

        //the two fields sent from Front-End
        Field fieldBefore = null;
        Field fieldAfter = null;

        //the same fields in the Back-End
        Field fieldBeforeBackEnd = null;
        Field fieldAfterBackEnd = null;

        int xBefore = -1, yBefore = -1, xAfter = -1, yAfter = -1;

        //finds the two positions of the fields sent from the Back-End
        for (Field field : after.getBoard().getFields()) {
            if (field.getWorker() != null) {
                fieldAfter = field;
                xAfter = fieldAfter.getPosX();
                yAfter = fieldAfter.getPosY();
            } else {
                fieldBefore = field;
                xBefore = fieldBefore.getPosX();
                yBefore = fieldBefore.getPosY();
            }
        }

        // faulty information by front-end
        if (fieldAfter == null || fieldBefore == null) {
            return false;
        }

        // finds the beforeField in the Back-End
        for (Field field : before.getBoard().getFields()) {
            if ((field.getPosX() == xBefore) && (field.getPosY() == yBefore)) {
                fieldBeforeBackEnd = field;
            }
        }

        // finds the after Field in the Back-End
        for (Field field : before.getBoard().getFields()) {
            if ((field.getPosX() == xAfter) && (field.getPosY() == yAfter)) {
                fieldAfterBackEnd = field;
            }
        }

        // faulty information by front-end
        if (fieldBeforeBackEnd == null || fieldAfterBackEnd == null) {
            return false;
        }

        int blockBefore = fieldBefore.getBlocks();
        int blockAfter = fieldAfter.getBlocks();

        //List of possible Neighboring Fields of the before field sent from Front-End
        ArrayList<Field> neighboringFields = new ArrayList<>();


        // finds neighbouring fields of the before field that was sent from Front-End and adds them to the ArrayList
        for (Field field : before.getBoard().getFields()) {
            if (field.getPosX() == xBefore + 1 || field.getPosX() == xBefore - 1 || field.getPosX() == xBefore) {
                if (field.getPosY() == yBefore + 1 || field.getPosY() == yBefore - 1 || field.getPosY() == yBefore) {
                    if (field.getPosX() != yBefore || field.getPosY() != yBefore) {
                        neighboringFields.add(field);
                    }
                }
            }
        }


        // For all Neighbouring Fields
        for (Field field : neighboringFields) {
            // Checks if the after field sent from Front-End is a direct Neighbouring field
            // -> normal move
            if ((field.getPosX() == xAfter) && (field.getPosY() == yAfter)) {
                // checks if the Worker's position is within the board's limitations
                if (xAfter >= 0 && xAfter <= 4 && yAfter >= 0 && yAfter <= 4) {
                    //Checks that it is possible to move to a neighbouring field
                    // it's free, if it has no dome, no worker,
                    // origin field had a worker
                    if ((fieldBeforeBackEnd.getWorker() != null)
                            // destination field is unoccupied
                            && (fieldAfterBackEnd.getWorker() == null)
                            // destination field has no dome
                            && (!fieldAfterBackEnd.getHasDome())
                            && (fieldBeforeBackEnd.getBlocks() == blockBefore)
                            && (fieldAfterBackEnd.getBlocks() == blockAfter)) {
                        // checks if number of blocks is within the game's limitations
                        if (blockAfter >= 0 && blockAfter <= 3) {

                            //check if blocks in after field is maximum 1 higher
                            if ((blockAfter <= blockBefore + 1)) {
                                isValid = true;
                            }
                        }
                    }
                }
            }
            // if after field is not neighboring field, checks if the rules for after Field are followed
            // -> special artemis move
            else {
                // checks if the Worker's position is within the board's limitations
                if (xAfter >= 0 && xAfter <= 4 && yAfter >= 0 && yAfter <= 4) {
                    // worker moved max one more, than the neighboring field of the before Field, on x-Axis
                    if (field.getPosX() + 1 == xAfter || field.getPosX() - 1 == xAfter || field.getPosX() == xAfter) {
                        // worker moved max one more, than the neighboring field of the before Field, on y-Axis
                        if (field.getPosY() == yAfter + 1 || field.getPosY() == yAfter - 1 || field.getPosY() == yAfter) {
                            //Checks that it is possible to move to a neighbouring field
                            // it's free, if it has no dome, no worker,
                            // origin field had a worker
                            if ((fieldBeforeBackEnd.getWorker() != null)
                                    // destination field is unoccupied
                                    && (fieldAfterBackEnd.getWorker() == null)
                                    // destination field has no dome
                                    && (!fieldAfterBackEnd.getHasDome())
                                    && (fieldBeforeBackEnd.getBlocks() == blockBefore)
                                    && (fieldAfterBackEnd.getBlocks() == blockAfter)) {
                                // checks if number of blocks is within the game's limitations
                                if (blockAfter >= 0 && blockAfter <= 3) {

                                    //check if blocks in after field is maximum 1 higher
                                    if ((blockAfter <= blockBefore + 1)) {
                                        isValid = true;
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }


        return isValid;
    }

}
