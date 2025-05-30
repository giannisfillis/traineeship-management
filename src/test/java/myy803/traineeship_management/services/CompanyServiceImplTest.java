package myy803.traineeship_management.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import myy803.traineeship_management.domainmodel.Company;
import myy803.traineeship_management.domainmodel.StudentEvaluation;
import myy803.traineeship_management.domainmodel.TraineeshipPosition;
import myy803.traineeship_management.mappers.CompanyMapper;
import myy803.traineeship_management.mappers.EvaluationMapper;
import myy803.traineeship_management.mappers.TraineeshipPositionMapper;

@SpringBootTest
@TestPropertySource(
  locations = "classpath:application.properties")
@AutoConfigureMockMvc
public class CompanyServiceImplTest {
	
	@Autowired
    private WebApplicationContext context;
	
	@Autowired
	private MockMvc mockMvc;

	  @Mock
	    private CompanyMapper companyMapper;

	    @Mock
	    private TraineeshipPositionMapper traineeshipPositionMapper;

	    @Mock
	    private EvaluationMapper evaluationMapper;

	    @InjectMocks
	    private CompanyServiceImpl companyService;

	    private Company company;
	    private TraineeshipPosition position;

	    @BeforeEach
	    void setUp() {
	    	mockMvc = MockMvcBuilders
	    	          .webAppContextSetup(context)
	    	          .build();
	        company = new Company();
	        company.setUsername("test_company");

	        position = new TraineeshipPosition();
	        position.setId(1);
	    }
	    
	    @Test
	    void testSaveCompany() {
	        companyService.save(company);
	        verify(companyMapper).save(company);
	    }

	    @Test
	    void testFindByUsername() {
	        when(companyMapper.findByUsername("test_company")).thenReturn(company);

	        Company result = companyService.findByUsername("test_company");

	        assertEquals(company, result);
	        verify(companyMapper).findByUsername("test_company");
	    }
	    
	    @Test
	    void testFindTraineeshipPositions() {
	        List<TraineeshipPosition> mockList = List.of(position);
	        when(traineeshipPositionMapper.findByOfferingCompany(company)).thenReturn(mockList);

	        List<TraineeshipPosition> result = companyService.findTraineeshipPositions(company);

	        assertEquals(mockList, result);
	    }
	    
	    @Test
	    void testFindAssignedTraineeshipPositions() {
	        List<TraineeshipPosition> assignedList = List.of(position);
	        when(traineeshipPositionMapper.findByOfferingCompanyAndIsAssigned(company, true)).thenReturn(assignedList);

	        List<TraineeshipPosition> result = companyService.findAssignedTraineeshipPositions(company);

	        assertEquals(assignedList, result);
	    }
	    
	    @Test
	    void testSavePosition() {
	        companyService.savePosition(position);
	        verify(traineeshipPositionMapper).save(position);
	    }

	    @Test
	    void testDeleteSpecificPosition() {
	        companyService.deleteSpecificPosition(1);
	        verify(traineeshipPositionMapper).deleteById(1);
	    }

	    
	    @Test
	    void testIsRegistered() {
	        assertTrue(companyService.isRegistered(company));
	        assertFalse(companyService.isRegistered(null));
	    }
	    
	    @Test
	    void testRetrievePositionForEvaluation() {
	        when(traineeshipPositionMapper.findById(1)).thenReturn(position);

	        TraineeshipPosition result = companyService.retrievePositionForEvaluation(1);

	        assertEquals(position, result);
	    }
	    
	    @Test
	    void testSaveTraineeshipEvaluation() {
	        StudentEvaluation evaluation = new StudentEvaluation();

	        companyService.saveTraineeshipEvaluation(position, evaluation);

	        assertEquals(position, evaluation.getEvaluatedPosition());
	        assertTrue(evaluation.isByCompany());
	        verify(evaluationMapper).save(evaluation);
	    }
	    
	    @Test
	    void testFindStudentEvaluationByPosition() {
	        StudentEvaluation eval = new StudentEvaluation();
	        Optional<StudentEvaluation> optEval = Optional.of(eval);

	        when(evaluationMapper.findByEvaluatedPositionAndByCompany(position, true)).thenReturn(optEval);

	        Optional<StudentEvaluation> result = companyService.findStudentEvaluationByPosition(position);

	        assertTrue(result.isPresent());
	        assertEquals(eval, result.get());
	    }





	    

}
