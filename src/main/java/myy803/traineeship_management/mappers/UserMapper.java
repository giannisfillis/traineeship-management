package myy803.traineeship_management.mappers;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import myy803.traineeship_management.domainmodel.User;

@Repository
public interface UserMapper extends JpaRepository<User, String>{
	
	Optional<User> findByUsername(String username);

}
