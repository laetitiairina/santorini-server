package ch.uzh.ifi.seal.soprafs19.repository;

import ch.uzh.ifi.seal.soprafs19.entity.Card;
import ch.uzh.ifi.seal.soprafs19.entity.Game;
import org.springframework.context.annotation.Primary;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Primary
@Repository("cardRepository")
public interface CardRepository extends CrudRepository<Card, Long> {
    Card findById(long id);
}