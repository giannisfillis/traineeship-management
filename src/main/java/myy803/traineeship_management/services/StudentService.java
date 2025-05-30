package myy803.traineeship_management.services;

import java.util.List;

import myy803.traineeship_management.domainmodel.Student;
import myy803.traineeship_management.domainmodel.TraineeshipPosition;


public interface StudentService {
	
	public List<Student> findAll();
	
	public Student findById(int theId);
	
	public void save(Student theStudent);
	
	public void deleteById(int theId);

	public Student findByUsername(String username);

	public boolean isRegistered(Student student);
	
	public TraineeshipPosition findPosition(Student student);
	
	public void saveLog(TraineeshipPosition position);
	
	public TraineeshipPosition findPositionById(int id);
	
	public List<TraineeshipPosition> retrievePositions();
}
