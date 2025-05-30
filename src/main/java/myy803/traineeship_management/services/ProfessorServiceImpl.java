package myy803.traineeship_management.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import myy803.traineeship_management.domainmodel.CompanyEvaluation;
import myy803.traineeship_management.domainmodel.Evaluation;
import myy803.traineeship_management.domainmodel.Professor;
import myy803.traineeship_management.domainmodel.StudentEvaluation;
import myy803.traineeship_management.domainmodel.TraineeshipPosition;
import myy803.traineeship_management.mappers.EvaluationMapper;
import myy803.traineeship_management.mappers.ProfessorMapper;
import myy803.traineeship_management.mappers.TraineeshipPositionMapper;

@Service
public class ProfessorServiceImpl implements ProfessorService{
	
	@Autowired
	private ProfessorMapper professorMapper;
	
	@Autowired
	private TraineeshipPositionMapper positionMapper;
	
	@Autowired
	private EvaluationMapper evaluationMapper;

	@Override
	public void save(Professor professor) {

		professorMapper.save(professor);
		
	}

	@Override
	public Professor findByUsername(String username) {

		return  professorMapper.findByUsername(username);
	}
	
	public boolean isRegistered(Professor professor) {
		return professor != null;
	}
	
	public List<TraineeshipPosition> retrievePositions(Professor professor){
		return positionMapper.findBySupervisor(professor);
	}
	
	public TraineeshipPosition retrievePositionForEvaluation(int positionId) {
		return positionMapper.findById(positionId);
	}
	
	public void saveStudentEvaluation(TraineeshipPosition position, StudentEvaluation studentEvaluation) {
		studentEvaluation.setEvaluatedPosition(position);
		((StudentEvaluation) studentEvaluation).setByCompany(false);
		evaluationMapper.save(studentEvaluation);
	}
	
	public void saveCompanyEvaluation(TraineeshipPosition position, CompanyEvaluation companyEvaluation) {
		companyEvaluation.setEvaluatedPosition(position);
		evaluationMapper.save(companyEvaluation);
	}

	
	public Optional<StudentEvaluation> findStudentEvaluationByPosition(TraineeshipPosition position) {
	    return evaluationMapper.findByEvaluatedPositionAndByCompany(position, false);
	}
	
	public Optional<CompanyEvaluation> findCompanyEvaluationByPosition(TraineeshipPosition position){
		return evaluationMapper.findCompanyEvaluationByEvaluatedPosition(position);
	}

}
