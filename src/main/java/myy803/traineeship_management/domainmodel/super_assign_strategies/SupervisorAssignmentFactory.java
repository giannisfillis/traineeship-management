package myy803.traineeship_management.domainmodel.super_assign_strategies;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SupervisorAssignmentFactory {
	
	@Autowired
	private AssignmentBasedOnInterests assignmentBasedOnInterests;
	
	@Autowired
	private AssignmentBasedOnLoad assignmentBasedOnLoad;
	
	public SupervisorAssignmentStrategy create(String strategy) {
		if(strategy.equals("Load")) {
			return assignmentBasedOnLoad;
		}
		else {
			return assignmentBasedOnInterests;
		}		
	}

}
