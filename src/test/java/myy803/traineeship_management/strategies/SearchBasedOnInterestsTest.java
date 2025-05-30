package myy803.traineeship_management.strategies;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import myy803.traineeship_management.domainmodel.Student;
import myy803.traineeship_management.domainmodel.TraineeshipPosition;
import myy803.traineeship_management.domainmodel.pos_search_strategies.SearchBasedOnInterests;
import myy803.traineeship_management.domainmodel.pos_search_strategies.SearchBasedOnLocation;
import myy803.traineeship_management.mappers.StudentMapper;
import myy803.traineeship_management.mappers.TraineeshipPositionMapper;

@ExtendWith(MockitoExtension.class)
public class SearchBasedOnInterestsTest {

    @Mock
    private StudentMapper studentMapper;
    
    @Mock
    private TraineeshipPositionMapper traineeshipPositionMapper;
    
    @InjectMocks
    private SearchBasedOnInterests searchBasedOnInterests;
    
    private Student testStudent;
    private List<TraineeshipPosition> testPositions;
    
    @BeforeEach
    public void setUp() {
      
        testStudent = new Student();
        testStudent.setInterests("Java, Spring, SQL");
        testStudent.setSkills("Programming, Teamwork, Communication");
        
        testPositions = new ArrayList<>();
        
        TraineeshipPosition position1 = new TraineeshipPosition();
        position1.setTopics("Java, Spring Boot");
        position1.setRequiredSkills("Programming, Java, Teamwork");
        position1.setAssigned(false);
        
        TraineeshipPosition position2 = new TraineeshipPosition();
        position2.setTopics("Python, Haskell");
        position2.setRequiredSkills("Python, Programming");
        position2.setAssigned(false);
        
        testPositions.add(position1);
        testPositions.add(position2);
    }
    
    @Test
    public void testSearchWithMatchingInterestsAndSkills() {
 
        String username = "testUser";
        
        when(studentMapper.findByUsername(username)).thenReturn(testStudent);
        when(traineeshipPositionMapper.findAll()).thenReturn(testPositions);
    
        List<TraineeshipPosition> result = searchBasedOnInterests.search(username);
        
        assertNotNull(result);
        assertEquals(1, result.size()); //only position1 should match
        
        TraineeshipPosition matchedPosition = result.get(0);
        assertEquals("Java, Spring Boot", matchedPosition.getTopics());
    }
    
    @Test
    public void testCalculatePositionJacardiValue() throws Exception {
 
        Set<String> interests = new HashSet<>(Arrays.asList("Java", "Spring", "SQL"));

        Method method = SearchBasedOnInterests.class.getDeclaredMethod(
            "calculatePositionJacardiValue", 
            Set.class, List.class
        );
        method.setAccessible(true);
  
        @SuppressWarnings("unchecked")
        Map<TraineeshipPosition, Double> results = (Map<TraineeshipPosition, Double>) 
            method.invoke(searchBasedOnInterests, interests, testPositions);
        
        assertEquals(2, results.size());
        
        // Jaccard = 1/4 = 0.25
        assertEquals(0.25, results.get(testPositions.get(0)), 0.001);
        
    
        // Jaccard = 0/5 = 0.0
        assertEquals(0.0, results.get(testPositions.get(1)), 0.001);
    }
    
    @Test
    public void testFindRecommendedPositions() throws Exception{
        Map<TraineeshipPosition, Double> testResults = new HashMap<>();
        testResults.put(testPositions.get(0), 0.5); //above threshold
        testResults.put(testPositions.get(1), 0.05); //below threshold
        
        Method method = SearchBasedOnInterests.class.getDeclaredMethod(
                "findRecommendedPositions", 
                Map.class
            );
        method.setAccessible(true);
        
        @SuppressWarnings("unchecked")
        List<TraineeshipPosition> result = (List<TraineeshipPosition>) 
            method.invoke(searchBasedOnInterests, testResults);
  
        assertEquals(1, result.size());
        assertEquals(testPositions.get(0), result.get(0));
    }
    
    @Test
    public void testFindPositionsMatchingSkills() throws Exception{

        List<String> studentSkills = Arrays.asList("Programming", "Teamwork", "Communication");
 
        Method method = SearchBasedOnInterests.class.getDeclaredMethod(
                "findPositionsMatchingSkills", 
                List.class, List.class
            );
        method.setAccessible(true);
        
        @SuppressWarnings("unchecked")
        List<TraineeshipPosition> result = (List<TraineeshipPosition>) 
            method.invoke(searchBasedOnInterests, testPositions, studentSkills);
        

        assertEquals(1, result.size());
        assertEquals(testPositions.get(0), result.get(0)); //only position1 matches >=2 skills
    }
    
    @Test
    public void testExtractStudentInterests() throws Exception {
        // Arrange
        String username = "testStudent";
        Student student = new Student();
        student.setInterests("Java, Spring, Software");
        
        when(studentMapper.findByUsername(username)).thenReturn(student);
        
        Method method = SearchBasedOnInterests.class.getDeclaredMethod(
                "extractStudentInterest", String.class
            );
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        Set<String> interests = (Set<String>) method.invoke(searchBasedOnInterests, username);
            
        // Assert
        assertEquals(Set.of("Java", "Spring", "Software"), interests);
    }
    
    @Test
    public void testExtractStudentSkills() throws Exception {
        // Arrange
        String username = "testStudent";
        Student student = new Student();
        student.setSkills("Programming, SQL, Java");
        
        when(studentMapper.findByUsername(username)).thenReturn(student);
        
        Method method = SearchBasedOnInterests.class.getDeclaredMethod(
                "extractStudentSkills", String.class
            );
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<String> skills = (List<String>) method.invoke(searchBasedOnInterests, username);
        
        // Assert
        assertEquals(List.of("Programming", "SQL", "Java"), skills);
    }
    
    @Test
    public void testExtractPositionSkills() throws Exception {
        // Arrange
        TraineeshipPosition position = new TraineeshipPosition();
        position.setRequiredSkills("Java, Spring, SQL");
        
        // Act via Reflection
        Method method = SearchBasedOnInterests.class.getDeclaredMethod(
            "extractPositionSkills", TraineeshipPosition.class
        );
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<String> skills = (List<String>) method.invoke(searchBasedOnInterests, position);
        
        // Assert
        assertEquals(List.of("Java", "Spring", "SQL"), skills);
    }
    
    @Test
    public void testFindPositionsMatchingSkillsNotEnoughSkillsMatch() throws Exception {
        // Arrange
        List<TraineeshipPosition> positions = testPositions;
        List<String> studentSkills = List.of("Hardware", "VHDL,"); // No matching skills
        
        // Act via Reflection
        Method method = SearchBasedOnInterests.class.getDeclaredMethod(
            "findPositionsMatchingSkills", List.class, List.class
        );
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<TraineeshipPosition> result = (List<TraineeshipPosition>) 
            method.invoke(searchBasedOnInterests, positions, studentSkills);
        
        // Assert
        assertTrue(result.isEmpty());
    }
    
    @Test
    public void testSearchNoInterestsMatch() {
        // Arrange
        String username = "testStudent";
        Student student = new Student();
        student.setInterests("Hardware, VHDL"); // Interests do not match
        student.setSkills("Programming, Teamwork, Communication");
        
   
        when(studentMapper.findByUsername(username)).thenReturn(student);
        when(traineeshipPositionMapper.findAll()).thenReturn(testPositions);
        
        // Act
        List<TraineeshipPosition> result = searchBasedOnInterests.search(username);
      
        assertTrue(result.isEmpty());
    }
    
    
    
}
