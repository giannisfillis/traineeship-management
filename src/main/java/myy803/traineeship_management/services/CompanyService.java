package myy803.traineeship_management.services;

import java.util.List;
import java.util.Optional;

import myy803.traineeship_management.domainmodel.Company;
import myy803.traineeship_management.domainmodel.Evaluation;
import myy803.traineeship_management.domainmodel.StudentEvaluation;
import myy803.traineeship_management.domainmodel.TraineeshipPosition;

public interface CompanyService {

	public void save(Company theCompany);
	
	public Company findByUsername(String username);
	
	public List<TraineeshipPosition> findTraineeshipPositions(Company company);
	
	public List<TraineeshipPosition> findAssignedTraineeshipPositions(Company theCompany);
	
	public void savePosition(TraineeshipPosition traineeshipPosition);

	public void deleteSpecificPosition(int posId);

	public boolean isRegistered(Company company);
	
	public TraineeshipPosition retrievePositionForEvaluation(int positionId);
	
	public void saveTraineeshipEvaluation(TraineeshipPosition position, Evaluation evaluation);
	
	public Optional<StudentEvaluation> findStudentEvaluationByPosition(TraineeshipPosition position);
}
