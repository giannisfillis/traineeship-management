package myy803.traineeship_management.domainmodel.super_assign_strategies;

import myy803.traineeship_management.domainmodel.Professor;

public interface SupervisorAssignmentStrategy {

	public Professor assign(int positionId);
}
