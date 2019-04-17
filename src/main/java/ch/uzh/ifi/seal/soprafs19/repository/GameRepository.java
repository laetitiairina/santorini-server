package ch.uzh.ifi.seal.soprafs19.repository;

import ch.uzh.ifi.seal.soprafs19.entity.Game;
import org.springframework.context.annotation.Primary;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Primary
@Repository("gameRepository")
public interface GameRepository extends CrudRepository<Game, Long> {
	//Game findByName(String name);

    // TODO ewa - Add methods
    Game findById(long gameId);
}
