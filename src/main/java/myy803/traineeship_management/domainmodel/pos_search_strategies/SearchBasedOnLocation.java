package myy803.traineeship_management.domainmodel.pos_search_strategies;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import myy803.traineeship_management.domainmodel.Student;
import myy803.traineeship_management.domainmodel.TraineeshipPosition;
import myy803.traineeship_management.mappers.StudentMapper;
import myy803.traineeship_management.mappers.TraineeshipPositionMapper;

@Component
public class SearchBasedOnLocation implements PositionSearchStrategy{
	
	@Autowired 
	private StudentMapper studentMapper;
	
	@Autowired
	private TraineeshipPositionMapper traineeshipPositionMapper;

	public List<TraineeshipPosition> search(String username){
		Student student = studentMapper.findByUsername(username);
		List<String> skillsList = extractStudentSkills(username);
		String studentLocation = student.getPreferredLocation();
		
		List<TraineeshipPosition> recommendedPositions = new ArrayList<>();
		List<TraineeshipPosition> traineeshipPositions = traineeshipPositionMapper.findByIsAssigned(false);
		for(TraineeshipPosition position :traineeshipPositions) {
			if(position.getCompanyLocation().equals(studentLocation)) {
				recommendedPositions.add(position);
			}
		}
		List<TraineeshipPosition> finalRecommendedPositions = findPositionsMatchingSkills(recommendedPositions,skillsList);
		return finalRecommendedPositions;
	}
	
	private List<String> extractStudentSkills(String username) {
		Student student = studentMapper.findByUsername(username);
		String skills = student.getSkills();
		String[] parts = skills.split(",");
		List<String> skillsList = new ArrayList<>();
		for (String part : parts) {
		    String trimmed = part.trim();
		    if (!trimmed.isEmpty()) {
		    	skillsList.add(trimmed);
		    }
		}
		return skillsList;
	}
	
	private List<String> extractPositionSkills(TraineeshipPosition position) {
		String skills = position.getRequiredSkills();
		String[] parts = skills.split(",");
		List<String> skillsList = new ArrayList<>();
		for (String part : parts) {
		    String trimmed = part.trim();
		    if (!trimmed.isEmpty()) {
		    	skillsList.add(trimmed);
		    }
		}
		return skillsList;
	}
	
	private List<TraineeshipPosition> findPositionsMatchingSkills(List<TraineeshipPosition> recommendedPositions, List<String> studentSkills){
		List<TraineeshipPosition> returnList = new ArrayList<>();
		int skillsMatched;
		for (TraineeshipPosition position : recommendedPositions) {
			skillsMatched = 0;
			List<String> positionSkills = extractPositionSkills(position);
			for (String posSkill : positionSkills) {
				for (String studSkill : studentSkills) {
					if (studSkill.equals(posSkill)){
						skillsMatched ++;
					}
				}
			}
			if (skillsMatched >= 2) {
				returnList.add(position);
			}
		}
		return returnList;
		
	}
}
