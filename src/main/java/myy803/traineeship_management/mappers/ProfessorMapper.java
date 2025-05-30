package myy803.traineeship_management.mappers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import myy803.traineeship_management.domainmodel.Professor;

@Repository
public interface ProfessorMapper extends JpaRepository<Professor, String>{

	Professor findByUsername(String username);
}
