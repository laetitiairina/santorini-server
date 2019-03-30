package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.entity.Board;
import ch.uzh.ifi.seal.soprafs19.repository.BoardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BoardService {

    private final Logger log = LoggerFactory.getLogger(BoardService.class);

    private final BoardRepository boardRepository;


    @Autowired
    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    /*
    public Iterable<Boards> getBoards() {
        return this.boardRepository.findAll();
    }
    */

    public Board createBoard() {
        Board newBoard = new Board();

        // TODO: set properties of newBoard

        boardRepository.save(newBoard);
        log.debug("Created Information for Board: {}", newBoard);
        return newBoard;
    }
}
