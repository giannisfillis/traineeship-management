package myy803.traineeship_management.mappers;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import myy803.traineeship_management.domainmodel.Company;
import myy803.traineeship_management.domainmodel.Professor;
import myy803.traineeship_management.domainmodel.Student;
import myy803.traineeship_management.domainmodel.TraineeshipPosition;

@Repository
public interface TraineeshipPositionMapper extends JpaRepository<TraineeshipPosition, Integer>{

	TraineeshipPosition findById(int id);
	List<TraineeshipPosition> findByIsAssigned(boolean isAssigned);
	List<TraineeshipPosition> findByOfferingCompany(Company offeringCompanyName);
	List<TraineeshipPosition> findByOfferingCompanyAndIsAssigned(Company theCompany, boolean isAssigned);
	TraineeshipPosition findByAssignedStudent(Student student);
	List<TraineeshipPosition> findBySupervisor(Professor professor);
	void deleteById(int posId);
}
