package ch.uzh.ifi.seal.soprafs19.repository;

import ch.uzh.ifi.seal.soprafs19.entity.Player;
import org.springframework.context.annotation.Primary;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Primary
@Repository("playerRepository")
public interface PlayerRepository extends CrudRepository<Player, Long> {
	Player findById(long id);
	Player findByToken(String token);
	List<Player> findByIsActive(Boolean isActive);
}
