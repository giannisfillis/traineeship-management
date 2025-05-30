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

import myy803.traineeship_management.domainmodel.CompanyEvaluation;
import myy803.traineeship_management.domainmodel.Professor;
import myy803.traineeship_management.domainmodel.StudentEvaluation;
import myy803.traineeship_management.domainmodel.TraineeshipPosition;
import myy803.traineeship_management.mappers.EvaluationMapper;
import myy803.traineeship_management.mappers.ProfessorMapper;
import myy803.traineeship_management.mappers.TraineeshipPositionMapper;

@SpringBootTest
@TestPropertySource(
  locations = "classpath:application.properties")
@AutoConfigureMockMvc
public class ProfessorServiceImplTest {

	@Autowired
    private WebApplicationContext context;
	
	@Autowired
	private MockMvc mockMvc;
	
    @Mock
    private ProfessorMapper professorMapper;

    @Mock
    private TraineeshipPositionMapper positionMapper;

    @Mock
    private EvaluationMapper evaluationMapper;

    @InjectMocks
    private ProfessorServiceImpl professorService;

    private Professor professor;
    private TraineeshipPosition position;
    private StudentEvaluation studentEval;
    private CompanyEvaluation companyEval;

    @BeforeEach
    void setUp() {
    	mockMvc = MockMvcBuilders
  	          .webAppContextSetup(context)
  	          .build();
        professor = new Professor();
        professor.setUsername("profX");

        position = new TraineeshipPosition();
        position.setId(1);

        studentEval = new StudentEvaluation();
        companyEval = new CompanyEvaluation();
    }

    @Test
    void testSaveProfessor() {
        professorService.save(professor);
        verify(professorMapper).save(professor);
    }

    @Test
    void testFindByUsername() {
        when(professorMapper.findByUsername("profX")).thenReturn(professor);

        Professor result = professorService.findByUsername("profX");

        assertEquals(professor, result);
        verify(professorMapper).findByUsername("profX");
    }

    @Test
    void testIsRegistered_WhenProfessorIsNotNull_ShouldReturnTrue() {
        assertTrue(professorService.isRegistered(professor));
    }

    @Test
    void testIsRegistered_WhenProfessorIsNull_ShouldReturnFalse() {
        assertFalse(professorService.isRegistered(null));
    }

    @Test
    void testRetrievePositions() {
        List<TraineeshipPosition> positions = List.of(position);
        when(positionMapper.findBySupervisor(professor)).thenReturn(positions);

        List<TraineeshipPosition> result = professorService.retrievePositions(professor);

        assertEquals(positions, result);
        verify(positionMapper).findBySupervisor(professor);
    }

    @Test
    void testRetrievePositionForEvaluation() {
        when(positionMapper.findById(1)).thenReturn(position);

        TraineeshipPosition result = professorService.retrievePositionForEvaluation(1);

        assertEquals(position, result);
        verify(positionMapper).findById(1);
    }

    @Test
    void testSaveStudentEvaluation() {
        professorService.saveStudentEvaluation(position, studentEval);

        assertEquals(position, studentEval.getEvaluatedPosition());
        assertFalse(studentEval.isByCompany());
        verify(evaluationMapper).save(studentEval);
    }

    @Test
    void testSaveCompanyEvaluation() {
        professorService.saveCompanyEvaluation(position, companyEval);

        assertEquals(position, companyEval.getEvaluatedPosition());
        verify(evaluationMapper).save(companyEval);
    }

    @Test
    void testFindStudentEvaluationByPosition() {
        when(evaluationMapper.findByEvaluatedPositionAndByCompany(position, false))
            .thenReturn(Optional.of(studentEval));

        Optional<StudentEvaluation> result = professorService.findStudentEvaluationByPosition(position);

        assertTrue(result.isPresent());
        assertEquals(studentEval, result.get());
    }

    @Test
    void testFindCompanyEvaluationByPosition() {
        when(evaluationMapper.findCompanyEvaluationByEvaluatedPosition(position))
            .thenReturn(Optional.of(companyEval));

        Optional<CompanyEvaluation> result = professorService.findCompanyEvaluationByPosition(position);

        assertTrue(result.isPresent());
        assertEquals(companyEval, result.get());
    }
}