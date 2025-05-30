package myy803.traineeship_management.services;

import java.util.List;
import java.util.Optional;

import myy803.traineeship_management.domainmodel.TraineeshipPosition;
import myy803.traineeship_management.domainmodel.CompanyEvaluation;
import myy803.traineeship_management.domainmodel.Professor;
import myy803.traineeship_management.domainmodel.Student;
import myy803.traineeship_management.domainmodel.StudentEvaluation;

public interface CommitteeService {

	public List<Student> findAppliedStudents();
	
	public List<TraineeshipPosition> findInProgressPositions();
	
	public void assignPositionToStudent(int posId, String username);
	
	public TraineeshipPosition findPosition(int id);
	
	public void assignSuperivisorToPosition(int posId, Professor professor);
	
	public Optional<StudentEvaluation> retrieveStudentEvaluationByCompanyForPosition(TraineeshipPosition position);
	
	public Optional<StudentEvaluation> retrieveStudentEvaluationByProfessorForPosition(TraineeshipPosition position);
	
	public Optional<CompanyEvaluation> retrieveCompanyEvaluationForPosition(TraineeshipPosition position);
	
	public void saveGrade(TraineeshipPosition position);

}