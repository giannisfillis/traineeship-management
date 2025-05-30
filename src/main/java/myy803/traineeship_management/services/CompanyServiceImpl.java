package myy803.traineeship_management.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import myy803.traineeship_management.domainmodel.Company;
import myy803.traineeship_management.domainmodel.Evaluation;
import myy803.traineeship_management.domainmodel.StudentEvaluation;
import myy803.traineeship_management.domainmodel.TraineeshipPosition;
import myy803.traineeship_management.mappers.CompanyMapper;
import myy803.traineeship_management.mappers.EvaluationMapper;
import myy803.traineeship_management.mappers.TraineeshipPositionMapper;

@Service
public class CompanyServiceImpl implements CompanyService{
	
	@Autowired
	private CompanyMapper companyMapper;
	
	@Autowired
	private TraineeshipPositionMapper traineeshipPositionMapper;
	
	@Autowired
	private EvaluationMapper evaluationMapper;

	@Override
	public void save(Company theCompany) {
		
			companyMapper.save(theCompany);
		
	}

	@Override
	public Company findByUsername(String username) {
		return companyMapper.findByUsername(username);
	}
	
	public List<TraineeshipPosition> findTraineeshipPositions(Company theCompany){
		
		return traineeshipPositionMapper.findByOfferingCompany(theCompany);
	}
	
	public List<TraineeshipPosition> findAssignedTraineeshipPositions(Company theCompany){
		return traineeshipPositionMapper.findByOfferingCompanyAndIsAssigned(theCompany,true);
	}

	@Override
	public void savePosition(TraineeshipPosition traineeshipPosition) {
		traineeshipPositionMapper.save(traineeshipPosition);
		
	}
	
	public void deleteSpecificPosition(int posId) {
		traineeshipPositionMapper.deleteById(posId);
	}
	
	public boolean isRegistered(Company company) {
		return company != null;
	}
	
	public TraineeshipPosition retrievePositionForEvaluation(int positionId) {
		return traineeshipPositionMapper.findById(positionId);
	}
	
	public void saveTraineeshipEvaluation(TraineeshipPosition position, Evaluation evaluation) {
		evaluation.setEvaluatedPosition(position);
		((StudentEvaluation) evaluation).setByCompany(true);
		evaluationMapper.save(evaluation);
	}
	
	public Optional<StudentEvaluation> findStudentEvaluationByPosition(TraineeshipPosition position) {
	    return evaluationMapper.findByEvaluatedPositionAndByCompany(position, true);
	}
}
