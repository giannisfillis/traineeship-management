package myy803.traineeship_management.mappers;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import myy803.traineeship_management.domainmodel.Student;

@Repository
public interface StudentMapper extends JpaRepository<Student, String>{
	
	Student findByUsername(String username);
	List<Student> findByAM(String AM);
	List<Student> findByPreferredLocation(String preferredLocation);
	List<Student> findByLookingForTraineeship(boolean lookingForTraineeship);

}
