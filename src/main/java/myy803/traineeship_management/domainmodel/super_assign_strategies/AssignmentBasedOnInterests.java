package myy803.traineeship_management.domainmodel.super_assign_strategies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import myy803.traineeship_management.domainmodel.Professor;
import myy803.traineeship_management.domainmodel.Student;
import myy803.traineeship_management.domainmodel.TraineeshipPosition;
import myy803.traineeship_management.mappers.ProfessorMapper;
import myy803.traineeship_management.mappers.TraineeshipPositionMapper;

@Component
public class AssignmentBasedOnInterests implements SupervisorAssignmentStrategy{

	@Autowired
	private ProfessorMapper professorMapper;
	
	@Autowired
	private TraineeshipPositionMapper positionMapper;
	
	public Professor assign(int positionId) {
		Set<String> topicsList = extractPositionTopics(positionId);

		Map<Professor,Double> results = new HashMap<>();
		List<Professor> professors = professorMapper.findAll();
		
		results = calculateProfessorJacardiValue(topicsList, professors);
		Professor recommendedProfessor = findRecommendedProfessors(results);
		if (recommendedProfessor == null) {
			System.out.println("Not matching professor found. Returning the first on the list.");
			return professors.get(0);
		}
		
		return recommendedProfessor;
	}
	
	private Professor findRecommendedProfessors(Map<Professor, Double> results) {
		double threshold = 0.1;
		Professor recommendedProfessor = null;
		Double maxValue = 0.0;
		for (Map.Entry<Professor, Double> entry : results.entrySet()) {
		    if (entry.getValue() >= threshold) {
		    	if(entry.getValue() > maxValue) {
		    		maxValue = entry.getValue();
		    		recommendedProfessor = entry.getKey();
		    	}
		    }
		}
		return recommendedProfessor;
	}

	private Map<Professor,Double> calculateProfessorJacardiValue(Set<String> topicsList, List<Professor> professors) {
		Map<Professor,Double> results = new HashMap<>();
		for(Professor professor : professors) {
			String interests = professor.getInterests();
			String[] partsInterests = interests.split(",");
			Set<String> interestsList = new HashSet<>();
			for (String part : partsInterests) {
				String trimmed = part.trim();
				if (!trimmed.isEmpty()) {
					interestsList.add(trimmed);
				}
			}

				Set<String> intersection = new HashSet<>(topicsList);
				intersection.retainAll(interestsList);
				Set<String> union = new HashSet<>(topicsList);
				union.addAll(interestsList);
				double jacardiValue = (union.isEmpty()) ? 0.0 : (double) intersection.size() / union.size();
				results.put(professor, jacardiValue);

		}
		return results;
	}

	private Set<String> extractPositionTopics(int id) {
		TraineeshipPosition position = positionMapper.findById(id);
		String topics = position.getTopics();
		String[] parts = topics.split(",");
		Set<String> topicsList = new HashSet<>();
		for (String part : parts) {
		    String trimmed = part.trim();
		    if (!trimmed.isEmpty()) {
		    	topicsList.add(trimmed);
		    }
		}
		return topicsList;
	}
	
}
