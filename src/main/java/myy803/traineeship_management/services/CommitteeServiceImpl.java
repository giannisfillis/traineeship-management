package myy803.traineeship_management.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import myy803.traineeship_management.domainmodel.CompanyEvaluation;
import myy803.traineeship_management.domainmodel.Professor;
import myy803.traineeship_management.domainmodel.Student;
import myy803.traineeship_management.domainmodel.StudentEvaluation;
import myy803.traineeship_management.domainmodel.TraineeshipPosition;
import myy803.traineeship_management.mappers.EvaluationMapper;
import myy803.traineeship_management.mappers.StudentMapper;
import myy803.traineeship_management.mappers.TraineeshipPositionMapper;

@Service
public class CommitteeServiceImpl implements CommitteeService{

	@Autowired
	private StudentMapper studentMapper;
	
	@Autowired
	private TraineeshipPositionMapper positionMapper;
	
	@Autowired
	private EvaluationMapper evaluationMapper;
	
	public List<Student> findAppliedStudents(){
		return studentMapper.findByLookingForTraineeship(true);
	}
	
	public List<TraineeshipPosition> findInProgressPositions(){
		return positionMapper.findByIsAssigned(true);
	}
	
	public void assignPositionToStudent(int posId, String username) {
		Student student = studentMapper.findByUsername(username);
		TraineeshipPosition position = positionMapper.findById(posId);
		position.setAssigned(true);
		position.setAssignedStudent(student);
		student.setLookingForTraineeship(false);
		positionMapper.save(position);
		studentMapper.save(student);
	}
	
	public TraineeshipPosition findPosition(int id) {
		return positionMapper.findById(id);
	}
	
	public void assignSuperivisorToPosition(int posId, Professor professor) {
		TraineeshipPosition position = positionMapper.findById(posId);
		position.setSupervisor(professor);
		positionMapper.save(position);
	}
	
	public Optional<StudentEvaluation> retrieveStudentEvaluationByCompanyForPosition(TraineeshipPosition position){
		
		return evaluationMapper.findByEvaluatedPositionAndByCompany(position,true);
	}
	
	public Optional<StudentEvaluation> retrieveStudentEvaluationByProfessorForPosition(TraineeshipPosition position){
		
		return evaluationMapper.findByEvaluatedPositionAndByCompany(position,false);
	}
	
	public Optional<CompanyEvaluation> retrieveCompanyEvaluationForPosition(TraineeshipPosition position){
		
		return evaluationMapper.findCompanyEvaluationByEvaluatedPosition(position);
	}
	
	public void saveGrade(TraineeshipPosition position) {
		positionMapper.save(position);
	}
	
	
	
}