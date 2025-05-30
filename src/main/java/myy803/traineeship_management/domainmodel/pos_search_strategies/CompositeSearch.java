package myy803.traineeship_management.domainmodel.pos_search_strategies;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import myy803.traineeship_management.domainmodel.TraineeshipPosition;

@Component
public class CompositeSearch implements PositionSearchStrategy{
	
	@Autowired 
	private SearchBasedOnInterests searchBasedOnInterests;
	
	@Autowired 
	private SearchBasedOnLocation searchBasedOnLocation;

	@Override
	public List<TraineeshipPosition> search(String username) {
		List<TraineeshipPosition> recommendedPositions = new ArrayList<>();
		recommendedPositions.addAll(searchBasedOnInterests.search(username));
	
		List<TraineeshipPosition> locationBasedPositions = searchBasedOnLocation.search(username);
		for(TraineeshipPosition position: locationBasedPositions) {
			if(!recommendedPositions.contains(position)) {
				recommendedPositions.add(position);
			}
		}
		return recommendedPositions;
	}

}
