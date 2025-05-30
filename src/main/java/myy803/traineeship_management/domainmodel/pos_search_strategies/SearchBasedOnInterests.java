package myy803.traineeship_management.domainmodel.pos_search_strategies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import myy803.traineeship_management.domainmodel.Student;
import myy803.traineeship_management.domainmodel.TraineeshipPosition;
import myy803.traineeship_management.mappers.StudentMapper;
import myy803.traineeship_management.mappers.TraineeshipPositionMapper;

@Component
public class SearchBasedOnInterests implements PositionSearchStrategy{
	
	@Autowired 
	private StudentMapper studentMapper;
	
	@Autowired
	private TraineeshipPositionMapper traineeshipPositionMapper;
	
	public List<TraineeshipPosition> search(String username) {
		Set<String> interestsList = extractStudentInterest(username);
		List<String> skillsList = extractStudentSkills(username);

		Map<TraineeshipPosition,Double> results = new HashMap<>();
		List<TraineeshipPosition> traineeshipPositions = traineeshipPositionMapper.findAll();
		
		results = calculatePositionJacardiValue(interestsList, traineeshipPositions);
		List<TraineeshipPosition> recommendedPositions = findRecommendedPositions(results);
		List<TraineeshipPosition> finalRecommendedPositions = findPositionsMatchingSkills(recommendedPositions,skillsList);
			
		return finalRecommendedPositions;
	}

	private List<TraineeshipPosition> findRecommendedPositions(Map<TraineeshipPosition, Double> results) {
		double threshold = 0.1;
		List<TraineeshipPosition> recommendedPositions = new ArrayList<>();
		for (Map.Entry<TraineeshipPosition, Double> entry : results.entrySet()) {
		    if (entry.getValue() >= threshold) {
		        recommendedPositions.add(entry.getKey());
		    }
		}
		return recommendedPositions;
	}

	private Map<TraineeshipPosition,Double> calculatePositionJacardiValue(Set<String> interestsList, List<TraineeshipPosition> traineeshipPositions) {
		Map<TraineeshipPosition,Double> results = new HashMap<>();
		for(TraineeshipPosition position :traineeshipPositions) {
			if(position.isAssigned() == false) {
				String topics = position.getTopics();
				String[] partsTopics = topics.split(",");
				Set<String> topicsList = new HashSet<>();
				for (String part : partsTopics) {
					String trimmed = part.trim();
					if (!trimmed.isEmpty()) {
						topicsList.add(trimmed);
					}
				}
	
					Set<String> intersection = new HashSet<>(interestsList);
					intersection.retainAll(topicsList);
					Set<String> union = new HashSet<>(interestsList);
					union.addAll(topicsList);
					double jacardiValue = (union.isEmpty()) ? 0.0 : (double) intersection.size() / union.size();
					results.put(position, jacardiValue);
		
			}
		}
		return results;
	}

	private Set<String> extractStudentInterest(String username) {
		Student student = studentMapper.findByUsername(username);
		String interests = student.getInterests();
		String[] parts = interests.split(",");
		Set<String> interestsList = new HashSet<>();
		for (String part : parts) {
		    String trimmed = part.trim();
		    if (!trimmed.isEmpty()) {
		    	interestsList.add(trimmed);
		    }
		}
		return interestsList;
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
