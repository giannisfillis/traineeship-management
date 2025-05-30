package myy803.traineeship_management.domainmodel.super_assign_strategies;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import myy803.traineeship_management.domainmodel.Professor;
import myy803.traineeship_management.domainmodel.TraineeshipPosition;
import myy803.traineeship_management.mappers.ProfessorMapper;
import myy803.traineeship_management.mappers.TraineeshipPositionMapper;

@Component
public class AssignmentBasedOnLoad implements SupervisorAssignmentStrategy{
	
	@Autowired
	private ProfessorMapper professorMapper;
	
	@Autowired
	private TraineeshipPositionMapper positionMapper;
	
	public Professor assign(int positionId) {
		
		TraineeshipPosition position = positionMapper.findById(positionId);
		List<TraineeshipPosition> allPositions = positionMapper.findAll();
		List<Professor> professors = professorMapper.findAll();
		int minLoad = Integer.MAX_VALUE;
		Professor minLoadProfessor = null;
		
		for (Professor professor : professors) {
			int load = 0;
			for (TraineeshipPosition traineeship: allPositions) {
				if (traineeship.getSupervisor() != null && traineeship.getSupervisor().getUsername().equals(professor.getUsername())) {
					load ++;
				}
			}
			if (load < minLoad) {
				minLoad = load;
				minLoadProfessor = professor;
			}
		}
		return minLoadProfessor;
		
	}

}
