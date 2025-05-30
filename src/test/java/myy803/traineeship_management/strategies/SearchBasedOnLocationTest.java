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

import myy803.traineeship_management.domainmodel.Company;
import myy803.traineeship_management.domainmodel.Student;
import myy803.traineeship_management.domainmodel.TraineeshipPosition;
import myy803.traineeship_management.domainmodel.pos_search_strategies.SearchBasedOnLocation;
import myy803.traineeship_management.mappers.StudentMapper;
import myy803.traineeship_management.mappers.TraineeshipPositionMapper;

@ExtendWith(MockitoExtension.class)
public class SearchBasedOnLocationTest {

	@Mock
    private StudentMapper studentMapper;
    
    @Mock
    private TraineeshipPositionMapper traineeshipPositionMapper;
    
    @InjectMocks
    private SearchBasedOnLocation searchBasedOnLocation;
    
    private Student testStudent;
    private TraineeshipPosition testPosition;
    private Company testCompany;
    
    
    @BeforeEach
    public void setUp() {
        testStudent = new Student();
        testStudent.setSkills("Java, Spring, SQL");
        testStudent.setPreferredLocation("Athens");
        
        testCompany = new Company();
        testCompany.setLocation("Athens");
        
        testPosition = new TraineeshipPosition();
        testPosition.setRequiredSkills("Java, AI, SQL");
        testPosition.setOfferingCompany(testCompany); 
        testPosition.setAssigned(false);
    }


    @Test
    public void testExtractStudentSkills() throws Exception {
        
        String username = "testUser";
        when(studentMapper.findByUsername(username)).thenReturn(testStudent);

        Method method = SearchBasedOnLocation.class.getDeclaredMethod(
            "extractStudentSkills", String.class
        );
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<String> skills = (List<String>) method.invoke(searchBasedOnLocation, username);
        
        assertEquals(3, skills.size());
        assertEquals("Java", skills.get(0));
        assertEquals("Spring", skills.get(1));
        assertEquals("SQL", skills.get(2));
    }



    @Test
    public void testExtractPositionSkills() throws Exception {

        testPosition.setRequiredSkills("Java, Spring, AI");
        
        Method method = SearchBasedOnLocation.class.getDeclaredMethod(
            "extractPositionSkills", TraineeshipPosition.class
        );
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<String> skills = (List<String>) method.invoke(searchBasedOnLocation, testPosition);
        
        assertEquals(3, skills.size());
        assertEquals("Java", skills.get(0));
        assertEquals("Spring", skills.get(1));
        assertEquals("AI", skills.get(2));
    }


    @Test
    public void testFindPositionsMatchingSkills() throws Exception {
        List<TraineeshipPosition> positions = List.of(testPosition);
        List<String> studentSkills = List.of("Java", "Spring", "SQL");
        
        Method method = SearchBasedOnLocation.class.getDeclaredMethod(
            "findPositionsMatchingSkills", List.class, List.class
        );
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<TraineeshipPosition> result = (List<TraineeshipPosition>) 
            method.invoke(searchBasedOnLocation, positions, studentSkills);
        
        assertEquals(1, result.size()); //matches on java - 1 skill
    }

    @Test
    public void testFindPositionsMatchingSkillsNotEnoughSkillsMatch() throws Exception {
        List<TraineeshipPosition> positions = List.of(testPosition);
        List<String> studentSkills = List.of("Python", "SQL"); //no matching skills
        
        Method method = SearchBasedOnLocation.class.getDeclaredMethod(
            "findPositionsMatchingSkills", List.class, List.class
        );
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<TraineeshipPosition> result = (List<TraineeshipPosition>) 
            method.invoke(searchBasedOnLocation, positions, studentSkills);
        
        assertTrue(result.isEmpty());
    }

    @Test
    public void testFindPositionsMatchingSkillsMultiplePositionsMatch() throws Exception {
   
        TraineeshipPosition position2 = new TraineeshipPosition();
        position2.setRequiredSkills("Python, SQL");
        Company company2 = new Company();
        company2.setLocation("Athens");
        position2.setOfferingCompany(company2);
        
        
        List<TraineeshipPosition> positions = List.of(testPosition, position2);
        List<String> studentSkills = List.of("Java", "Python", "SQL");
        
        Method method = SearchBasedOnLocation.class.getDeclaredMethod(
            "findPositionsMatchingSkills", List.class, List.class
        );
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<TraineeshipPosition> result = (List<TraineeshipPosition>) 
            method.invoke(searchBasedOnLocation, positions, studentSkills);
        
        assertEquals(2, result.size()); 
    }

 
    @Test
    public void testSearchLocationMatch() {
        // Arrange
        String username = "testUser";
        when(studentMapper.findByUsername(username)).thenReturn(testStudent);
        
        Company company = new Company();
        company.setLocation("Athens");
        
        TraineeshipPosition matchingPosition = new TraineeshipPosition();
        matchingPosition.setRequiredSkills("Java, SQL");
        matchingPosition.setOfferingCompany(company);
        matchingPosition.setAssigned(false);
        
        when(traineeshipPositionMapper.findByIsAssigned(false))
            .thenReturn(List.of(matchingPosition));
        
        // Act
        List<TraineeshipPosition> result = searchBasedOnLocation.search(username);
        
        // Assert
        assertEquals(1, result.size());
        assertEquals(matchingPosition, result.get(0));
    }
    
    @Test
    public void testSearchLocationDoesNotMatch() {
        // Arrange
        String username = "testUser";
        when(studentMapper.findByUsername(username)).thenReturn(testStudent);
        
        Company company = new Company();
        company.setLocation("Thessaloniki"); 
        
        TraineeshipPosition nonMatchingPosition = new TraineeshipPosition();
        nonMatchingPosition.setRequiredSkills("Java, SQL");
        nonMatchingPosition.setOfferingCompany(company);
        nonMatchingPosition.setAssigned(false);
        
        when(traineeshipPositionMapper.findByIsAssigned(false))
            .thenReturn(List.of(nonMatchingPosition));
        
        // Act
        List<TraineeshipPosition> result = searchBasedOnLocation.search(username);
        
        // Assert
        assertTrue(result.isEmpty());
    }
    
  
    
}