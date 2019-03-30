package ch.uzh.ifi.seal.soprafs19.repository;

import ch.uzh.ifi.seal.soprafs19.entity.Board;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository("boardRepository")
public interface BoardRepository extends CrudRepository<Board, Long> {
	//Board findByName(String name);
}
