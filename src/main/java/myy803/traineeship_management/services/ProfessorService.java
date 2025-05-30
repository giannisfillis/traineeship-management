package myy803.traineeship_management.services;

import java.util.List;
import java.util.Optional;

import myy803.traineeship_management.domainmodel.CompanyEvaluation;
import myy803.traineeship_management.domainmodel.Evaluation;
import myy803.traineeship_management.domainmodel.Professor;
import myy803.traineeship_management.domainmodel.StudentEvaluation;
import myy803.traineeship_management.domainmodel.TraineeshipPosition;

public interface ProfessorService {

	void save(Professor professor);

	Professor findByUsername(String username);

	public boolean isRegistered(Professor professor);
	
	public List<TraineeshipPosition> retrievePositions(Professor professor);
	
	public TraineeshipPosition retrievePositionForEvaluation(int positionId);
	
	public void saveStudentEvaluation(TraineeshipPosition position, StudentEvaluation studentEvaluation);
	
	public void saveCompanyEvaluation(TraineeshipPosition position, CompanyEvaluation companyEvaluation);
	
	public Optional<StudentEvaluation> findStudentEvaluationByPosition(TraineeshipPosition position);
	
	public Optional<CompanyEvaluation> findCompanyEvaluationByPosition(TraineeshipPosition position);

}
