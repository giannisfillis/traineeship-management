package myy803.traineeship_management.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import myy803.traineeship_management.domainmodel.Student;
import myy803.traineeship_management.domainmodel.TraineeshipPosition;
import myy803.traineeship_management.mappers.StudentMapper;
import myy803.traineeship_management.mappers.TraineeshipPositionMapper;

@Service
public class StudentServiceImpl implements StudentService{
	
	@Autowired
	private StudentMapper studentMapper;
	
	@Autowired
	private TraineeshipPositionMapper positionMapper;

	@Override
	public List<Student> findAll() {

		return studentMapper.findAll();
	}

	@Override
	public Student findById(int theId) {

		return null;
	}

	@Override
	public void save(Student theStudent) {
		
		studentMapper.save(theStudent);
	}

	@Override
	public void deleteById(int theId) {

	}

	@Override
	public Student findByUsername(String username) {

		return studentMapper.findByUsername(username);
	}
	
	public boolean isRegistered(Student student) {
		return student != null;
	}
	
	public TraineeshipPosition findPosition(Student student) {
		return positionMapper.findByAssignedStudent(student);
	}
	
	public void saveLog(TraineeshipPosition position) {
		positionMapper.save(position);
	}
	
	public TraineeshipPosition findPositionById(int id) {
	    return positionMapper.findById(id);
	}
	
	public List<TraineeshipPosition> retrievePositions() {
		return positionMapper.findAll();
	}

}
