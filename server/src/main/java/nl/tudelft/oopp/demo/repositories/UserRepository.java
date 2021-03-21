package nl.tudelft.oopp.demo.repositories;

import java.util.List;

import nl.tudelft.oopp.demo.entities.Room;
import nl.tudelft.oopp.demo.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository<T extends User> extends JpaRepository<T, Long> {

    List<T> findAllByRoomRoomId(long roomId);

    void deleteById(Long userId);


}
