package myy803.traineeship_management.strategies;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import myy803.traineeship_management.domainmodel.Professor;
import myy803.traineeship_management.domainmodel.TraineeshipPosition;
import myy803.traineeship_management.domainmodel.super_assign_strategies.AssignmentBasedOnLoad;
import myy803.traineeship_management.mappers.ProfessorMapper;
import myy803.traineeship_management.mappers.TraineeshipPositionMapper;

@ExtendWith(MockitoExtension.class)
public class AssignmentBasedOnLoadTest {

    @Mock
    private ProfessorMapper professorMapper;
    
    @Mock
    private TraineeshipPositionMapper positionMapper;
    
    @InjectMocks
    private AssignmentBasedOnLoad assignmentStrategy;

    
    
    @Test
    public void testAssignReturnsProfessorWithMinLoad() {

        int positionId = 100;
        
        Professor professor1 = new Professor();
        professor1.setUsername("prof1");
        Professor professor2 = new Professor();
        professor2.setUsername("prof2");
        
        TraineeshipPosition pos1 = new TraineeshipPosition();
        pos1.setSupervisor(professor1); 
        TraineeshipPosition pos2 = new TraineeshipPosition();
        pos2.setSupervisor(professor1); //prof 1 has 2 load
        TraineeshipPosition pos3 = new TraineeshipPosition();
        pos3.setSupervisor(null);
        
        when(positionMapper.findById(positionId)).thenReturn(new TraineeshipPosition());
        when(positionMapper.findAll()).thenReturn(Arrays.asList(pos1, pos2, pos3));
        when(professorMapper.findAll()).thenReturn(Arrays.asList(professor1, professor2));
        
        Professor result = assignmentStrategy.assign(positionId);

        assertEquals(professor2, result); //prof2 has 0 load
    }
    
    @Test
    public void testAssignWhenEqualLoad() {
        Professor professor1 = new Professor();
        professor1.setUsername("prof1");
        Professor professor2 = new Professor();
        professor2.setUsername("prof2");
        
        TraineeshipPosition pos1 = new TraineeshipPosition();
        pos1.setSupervisor(professor1);
        TraineeshipPosition pos2 = new TraineeshipPosition();
        pos2.setSupervisor(professor2);
        
        when(positionMapper.findById(anyInt())).thenReturn(new TraineeshipPosition());
        when(positionMapper.findAll()).thenReturn(Arrays.asList(pos1, pos2));
        when(professorMapper.findAll()).thenReturn(Arrays.asList(professor1, professor2));
        
        Professor result = assignmentStrategy.assign(1);
 
        assertEquals(professor1, result); //returns first professor in list
    }
     
    
    
}
