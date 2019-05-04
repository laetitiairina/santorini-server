package ch.uzh.ifi.seal.soprafs19.rules;

import ch.uzh.ifi.seal.soprafs19.entity.Field;
import ch.uzh.ifi.seal.soprafs19.entity.Game;
import ch.uzh.ifi.seal.soprafs19.entity.Player;
import ch.uzh.ifi.seal.soprafs19.entity.Worker;
import ch.uzh.ifi.seal.soprafs19.entity.Board;
import java.util.ArrayList;

public class SimpleRuleSet implements IRuleSet {

    public Boolean checkMovePhase(Game before, Game after) {
        boolean isValid = false;

        Board board = new Board();

        Field fieldBefore = new Field(board,0, 0);
        Field fieldAfter = new Field(board, 0, 0);

        Field FieldBeforeBackEnd = new Field(board, 0, 0);
        Field FieldAfterBackEnd = new Field(board, 0, 0);

        ArrayList<Integer> posBefore = new ArrayList<>();
        ArrayList<Integer> posAfter = new ArrayList<>();

        //adds the position of the two fields to the array and declares which is the before and after field
        for (Field field : after.getBoard().getFields()) {
            if (field.getWorker() != null) {
                fieldAfter = field;
                posAfter.add(fieldAfter.getPosX());
                posAfter.add(fieldAfter.getPosY());
            } else {
                fieldBefore = field;
                posBefore.add(fieldBefore.getPosX());
                posBefore.add(fieldBefore.getPosY());
            }
        }

        for (Field field : before.getBoard().getFields()) {
            if ((field.getPosX() == fieldBefore.getPosX()) && (field.getPosY() == fieldBefore.getPosY())) {
                FieldBeforeBackEnd = field;
            }
        }

        for (Field field : before.getBoard().getFields()) {
            if ((field.getPosX() == fieldAfter.getPosX()) && (field.getPosY() == fieldAfter.getPosY())) {
                FieldAfterBackEnd = field;
            }
        }


        int BlockBefore = fieldBefore.getBlocks();
        int BlockAfter = fieldAfter.getBlocks();

        int Xbefore = posBefore.get(0);
        int Ybefore = posBefore.get(1);

        int Xafter = posAfter.get(0);
        int Yafter = posAfter.get(1);


        // checks if the Worker's position is within the board's limitations
        if (Xafter >= 0 && Xafter <= 4 && Yafter >= 0 && Yafter <= 4) {

            // move 1 or 0 fields on X-axis
            if ((Xafter == Xbefore) || (Xafter == Xbefore - 1) || (Xafter == Xbefore + 1)) {

                // move 1 or 0 fields on Y-axis
                if ((Yafter == Ybefore) || (Yafter == Ybefore - 1) || (Yafter == Ybefore + 1)) {

                    // not allowed to stay on same field
                    if ((Xafter != Xbefore) || (Yafter != Ybefore)) {

                        // origin field had a worker
                        if ((FieldBeforeBackEnd.getWorker() != null)
                                // destination field is unoccupied
                                && (FieldAfterBackEnd.getWorker() == null)
                                // destination field has no dome
                                && (!FieldAfterBackEnd.getHasDome())
                                && (FieldBeforeBackEnd.getBlocks() == BlockBefore)
                                && (FieldAfterBackEnd.getBlocks() == BlockAfter)) {
                            // checks if number of blocks is within the game's limitations
                            if (BlockAfter >= 0 && BlockAfter <= 3) {

                                //check if blocks in after field is maximum 1 higher
                                if ((BlockAfter <= BlockBefore + 1)) {
                                    isValid = true;
                                }
                            }
                        }
                    }
                }
            }
        }

        return isValid;
    }

    public Boolean checkBuildPhase(Game before, Game after) {

        boolean isValid = false;

        Board board = new Board();

        Field FieldAfterBuilt = new Field(board, 0, 0);
        Field FieldBuiltOnBackEnd = new Field(board, 0, 0);

        ArrayList<Integer> FieldAfterBuiltPosition = new ArrayList<>();
        ArrayList<Integer> PositionOfWorker = new ArrayList<>();

        //adds the position of the Field that has been sent from the front-end
        for (Field field : after.getBoard().getFields()) {
            if (field != null) {
                FieldAfterBuilt = field;
                FieldAfterBuiltPosition.add(FieldAfterBuilt.getPosX());
                FieldAfterBuiltPosition.add(FieldAfterBuilt.getPosY());
            }
        }

        //gets the Position of the current Worker which is building a block
        for (Player p : before.getPlayers()) {
            if (p.getIsCurrentPlayer()) {
                for (Worker w : p.getWorkers()) {
                    if (w.getIsCurrentWorker()) {
                        PositionOfWorker.add(w.getField().getPosX());
                        PositionOfWorker.add(w.getField().getPosY());
                    }
                }
            }
        }

        for (Field field : before.getBoard().getFields()) {
            if ((field.getPosX() == FieldAfterBuilt.getPosX()) && (field.getPosY() == FieldAfterBuilt.getPosY())) {
                FieldBuiltOnBackEnd = field;
            }
        }

        int PosWorkerX = PositionOfWorker.get(0);
        int PosWorkerY = PositionOfWorker.get(1);

        int PosBuiltFieldX = FieldAfterBuiltPosition.get(0);
        int PosBuiltFieldY = FieldAfterBuiltPosition.get(1);


        // Built on 1 or 0 fields next to the worker on X-axis
        if ((PosBuiltFieldX == PosWorkerX) || (PosBuiltFieldX == PosWorkerX - 1) || (PosBuiltFieldX == PosWorkerX + 1)) {

            // Built on 1 or 0 fields next to the worker on Y-axis
            if ((PosBuiltFieldY == PosWorkerY) || (PosBuiltFieldY == PosWorkerY - 1) || (PosBuiltFieldY == PosWorkerY + 1)) {

                // can not built on the same field as the worker
                if ((PosBuiltFieldX != PosWorkerX) || (PosBuiltFieldY != PosWorkerY)) {

                    //check if the field which is built on doesn't have a worker, dome and that a player only built one block up
                    if ((FieldBuiltOnBackEnd.getWorker() == null) && !(FieldBuiltOnBackEnd.getHasDome()) && (FieldBuiltOnBackEnd.getBlocks() == FieldAfterBuilt.getBlocks() - 1) && (FieldAfterBuilt.getBlocks()<=3)) {

                        //check if building a dome is valid
                        if(FieldAfterBuilt.getHasDome() && FieldBuiltOnBackEnd.getBlocks() == 3) {

                            isValid = true;

                        }

                    }
                }
            }
        }

        return isValid;
    }

    //Method that Checks if a Worker Can not move anymore
    public Boolean WorkerCanNotMove(Worker worker, Game game){

        boolean isValid = false;

        int PosX = worker.getField().getPosX();
        int PosY = worker.getField().getPosY();


        ArrayList <Field> NeighbouringFields = new ArrayList<>();

        //Adds neighbouring fields to the array
        for(Field field : game.getBoard().getFields()) {
            if (field.getPosX() == PosX + 1 || field.getPosX() == PosX - 1 || field.getPosX() == PosX) {
                if (field.getPosY() == PosY + 1 || field.getPosY() == PosY - 1 || field.getPosY() == PosY) {
                    if (field.getPosX() >= 0 && field.getPosX() <= 4 || field.getPosY() <= 4 && field.getPosY() >= 0) {
                        if (field.getPosX() != PosX || field.getPosY() != PosY) {
                            NeighbouringFields.add(field);
                        }
                    }
                }
            }
        }

        //Checks that it is not possible to move to a neighbouring field
        for(Field field : NeighbouringFields){
            if(field.getHasDome() && field.getWorker()!= null){
                if(field.getBlocks()>= (worker.getField().getBlocks()+2)){
                    isValid= true;
                }

            }
        }

        return isValid;
    }

    public Boolean checkWinCondition(Game game) {

        boolean isValid = false;

        Player P1 = new Player();
        Player P2 = new Player();

        Worker W1 = P1.getWorkers().get(0);
        Worker W2 = P1.getWorkers().get(1);
        Worker W3 = P2.getWorkers().get(0);
        Worker W4 = P2.getWorkers().get(1);


        //Case when the Current Player wins
        for (Player p: game.getPlayers() ) {
            if(p.getIsCurrentPlayer()){
                P1 = p;
                for (Worker w: P1.getWorkers()){
                    if((w.getField().getBlocks() == 3 && !w.getField().getHasDome()) || (WorkerCanNotMove(W3,game)&& WorkerCanNotMove(W4,game))){
                        isValid = true;
            }

        }

            }
            }

        //Case when not the Current Player wins
        if((WorkerCanNotMove(W1,game)&& WorkerCanNotMove(W2,game))){
            isValid = true;
            }
        

        return isValid;
    }


    public Boolean hasRuleForOpponentsTurn() {
        return false;
    }
}
