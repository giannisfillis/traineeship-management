package myy803.traineeship_management.strategies;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.lang.reflect.Method;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import myy803.traineeship_management.domainmodel.Professor;
import myy803.traineeship_management.domainmodel.TraineeshipPosition;
import myy803.traineeship_management.domainmodel.super_assign_strategies.AssignmentBasedOnInterests;
import myy803.traineeship_management.mappers.ProfessorMapper;
import myy803.traineeship_management.mappers.TraineeshipPositionMapper;

@ExtendWith(MockitoExtension.class)
public class AssignmentBasedOnInterestsTest {

    @Mock
    private ProfessorMapper professorMapper;
    
    @Mock
    private TraineeshipPositionMapper positionMapper;
    
    @InjectMocks
    private AssignmentBasedOnInterests assignmentStrategy;

    
    
    @Test
    public void testAssignMatchExists() {
    
        int positionId = 100;
        TraineeshipPosition position = new TraineeshipPosition();
        position.setTopics("Java, Spring");
        
        Professor professor1 = new Professor();
        professor1.setInterests("Python, AI");
        Professor professor2 = new Professor();
        professor2.setInterests("Java, Spring, SQL"); 
        when(positionMapper.findById(positionId)).thenReturn(position);
        when(professorMapper.findAll()).thenReturn(List.of(professor1, professor2));
     
        Professor result = assignmentStrategy.assign(positionId);
     
        assertEquals(professor2, result);
    }
    
    @Test
    public void testExtractPositionTopics() throws Exception {

        int positionId = 1;
        TraineeshipPosition position = new TraineeshipPosition();
        position.setTopics("  Java, , SQL  ");
        
        when(positionMapper.findById(positionId)).thenReturn(position);
      
        Method method = AssignmentBasedOnInterests.class.getDeclaredMethod(
            "extractPositionTopics", int.class
        );
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        Set<String> topics = (Set<String>) method.invoke(assignmentStrategy, positionId);
  
        assertEquals(2, topics.size());
        assertTrue(topics.contains("Java"));
        assertTrue(topics.contains("SQL"));
    }
    
    @Test
    public void testCalculateProfessorJacardiValue() throws Exception {
        // Arrange
        Set<String> positionTopics = Set.of("Java", "Spring");
        
        Professor professor1 = new Professor();
        professor1.setInterests("Java, SQL"); //1/3 similarity
        Professor professor2 = new Professor();
        professor2.setInterests("Python, Haskell"); //0 similarity

        Method method = AssignmentBasedOnInterests.class.getDeclaredMethod(
            "calculateProfessorJacardiValue", Set.class, List.class
        );
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Professor, Double> results = (Map<Professor, Double>) 
            method.invoke(assignmentStrategy, positionTopics, List.of(professor1, professor2));
    
        assertEquals(2, results.size());
        assertEquals(0.333, results.get(professor1), 0.001); // 1/3 matching
        assertEquals(0.0, results.get(professor2), 0.001);
    }
    
    @Test
    public void testFindRecommendedProfessors() throws Exception {
        Map<Professor, Double> testData = new HashMap<>();
        Professor professor1 = new Professor();
        Professor professor2 = new Professor(); 
        Professor professor3 = new Professor();
        professor1.setUsername("Professor 1");
        professor2.setUsername("Professor 2");
        professor3.setUsername("Professor 3");
        
        testData.put(professor1, 0.05); //below threshold
        testData.put(professor2, 0.15);  //above threshold
        testData.put(professor3, 0.25);  //best match
        
        Method method = AssignmentBasedOnInterests.class.getDeclaredMethod(
            "findRecommendedProfessors", Map.class
        );
        method.setAccessible(true);
        Professor result = (Professor) method.invoke(assignmentStrategy, testData);
        
        assertEquals(professor3, result); //prof3 has the best score
    }
    
    @Test
    public void testAssignNoProfessorMeetsThreshold() {
 
        int positionId = 100;
        TraineeshipPosition position = new TraineeshipPosition();
        position.setTopics("Java, Spring");
        
        Professor professor1 = new Professor();
        professor1.setInterests("Python, AI"); // not matching
        
        Professor professor2 = new Professor();
        professor2.setInterests("C++, Algorithms"); // not matching
        
        when(positionMapper.findById(positionId)).thenReturn(position);
        when(professorMapper.findAll()).thenReturn(List.of(professor1, professor2));

        Professor result = assignmentStrategy.assign(positionId);

        assertEquals(professor1, result); //returns the first on the list
    }
    
    
}