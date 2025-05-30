package myy803.traineeship_management.domainmodel.pos_search_strategies;
import java.util.List;

import myy803.traineeship_management.domainmodel.TraineeshipPosition;

public interface PositionSearchStrategy {
		
	public List<TraineeshipPosition> search(String username);
}
