package myy803.traineeship_management.strategies;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import myy803.traineeship_management.domainmodel.super_assign_strategies.AssignmentBasedOnInterests;
import myy803.traineeship_management.domainmodel.super_assign_strategies.AssignmentBasedOnLoad;
import myy803.traineeship_management.domainmodel.super_assign_strategies.SupervisorAssignmentFactory;
import myy803.traineeship_management.domainmodel.super_assign_strategies.SupervisorAssignmentStrategy;

@ExtendWith(MockitoExtension.class)
public class SupervisorAssignmentFactoryTest {

    @Mock
    private AssignmentBasedOnInterests assignmentBasedOnInterests;
    
    @Mock
    private AssignmentBasedOnLoad assignmentBasedOnLoad;
    
    @InjectMocks
    private SupervisorAssignmentFactory factory;

    
    @Test
    public void testCreateLoadStrategy() {
     
        SupervisorAssignmentStrategy strategy = factory.create("Load");
        assertSame(assignmentBasedOnLoad, strategy);
    }
    
    @Test
    public void testCreateInterestsStrategyOnAnythingElse() {

        SupervisorAssignmentStrategy strategy1 = factory.create("Interests");
        SupervisorAssignmentStrategy strategy2 = factory.create("Anything Else");
        SupervisorAssignmentStrategy strategy3 = factory.create("");
        
        //returns based on interests strategy on any other case
        assertSame(assignmentBasedOnInterests, strategy1);
        assertSame(assignmentBasedOnInterests, strategy2);
        assertSame(assignmentBasedOnInterests, strategy3);
    }
    
}