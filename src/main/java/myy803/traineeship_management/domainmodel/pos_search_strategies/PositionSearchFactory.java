package myy803.traineeship_management.domainmodel.pos_search_strategies;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import myy803.traineeship_management.domainmodel.TraineeshipPosition;

@Component
public class PositionSearchFactory {
	
	@Autowired 
	private SearchBasedOnInterests searchBasedOnInterests;
	
	@Autowired 
	private SearchBasedOnLocation searchBasedOnLocation;
	
	@Autowired
	private CompositeSearch compositeSearch;
	

	public PositionSearchStrategy create(String strategy) {
		if(strategy.equals("Location")) {
			return searchBasedOnLocation;
		}
		else if (strategy.equals("Interests")) {
			return searchBasedOnInterests;
		}
		else {
			return compositeSearch;
		}
	}
}
