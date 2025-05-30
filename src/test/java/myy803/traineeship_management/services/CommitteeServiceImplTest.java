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
import myy803.traineeship_management.domainmodel.Student;
import myy803.traineeship_management.domainmodel.StudentEvaluation;
import myy803.traineeship_management.domainmodel.TraineeshipPosition;
import myy803.traineeship_management.mappers.EvaluationMapper;
import myy803.traineeship_management.mappers.StudentMapper;
import myy803.traineeship_management.mappers.TraineeshipPositionMapper;

@SpringBootTest
@TestPropertySource(
  locations = "classpath:application.properties")
@AutoConfigureMockMvc
public class CommitteeServiceImplTest {
	
	@Autowired
    private WebApplicationContext context;
	
	@Autowired
	private MockMvc mockMvc;

    @Mock
    private StudentMapper studentMapper;

    @Mock
    private TraineeshipPositionMapper positionMapper;

    @Mock
    private EvaluationMapper evaluationMapper;

    @InjectMocks
    private CommitteeServiceImpl committeeService;

    private Student student;
    private TraineeshipPosition position;
    private Professor professor;
    private StudentEvaluation studentEvaluation;
    private CompanyEvaluation companyEvaluation;

    @BeforeEach
    void setUp() {
    	mockMvc = MockMvcBuilders
  	          .webAppContextSetup(context)
  	          .build();
        student = new Student();
        student.setUsername("john");
        student.setLookingForTraineeship(true);

        position = new TraineeshipPosition();
        position.setId(1);
        position.setAssigned(false);

        professor = new Professor();
        professor.setUsername("prof1");

        studentEvaluation = new StudentEvaluation();
        companyEvaluation = new CompanyEvaluation();
    }

    @Test
    void testFindAppliedStudents() {
        List<Student> students = List.of(student);
        when(studentMapper.findByLookingForTraineeship(true)).thenReturn(students);

        List<Student> result = committeeService.findAppliedStudents();

        assertEquals(students, result);
        verify(studentMapper).findByLookingForTraineeship(true);
    }

    @Test
    void testFindInProgressPositions() {
        List<TraineeshipPosition> positions = List.of(position);
        when(positionMapper.findByIsAssigned(true)).thenReturn(positions);

        List<TraineeshipPosition> result = committeeService.findInProgressPositions();

        assertEquals(positions, result);
        verify(positionMapper).findByIsAssigned(true);
    }

    @Test
    void testAssignPositionToStudent() {
        when(studentMapper.findByUsername("john")).thenReturn(student);
        when(positionMapper.findById(1)).thenReturn(position);

        committeeService.assignPositionToStudent(1, "john");

        assertTrue(position.isAssigned());
        assertEquals(student, position.getAssignedStudent());
        assertFalse(student.isLookingForTraineeship());

        verify(positionMapper).save(position);
        verify(studentMapper).save(student);
    }

    @Test
    void testFindPosition() {
        when(positionMapper.findById(1)).thenReturn(position);

        TraineeshipPosition result = committeeService.findPosition(1);

        assertEquals(position, result);
        verify(positionMapper).findById(1);
    }

    @Test
    void testAssignSupervisorToPosition() {
        when(positionMapper.findById(1)).thenReturn(position);

        committeeService.assignSuperivisorToPosition(1, professor);

        assertEquals(professor, position.getSupervisor());
        verify(positionMapper).save(position);
    }

    @Test
    void testRetrieveStudentEvaluationByCompanyForPosition() {
        when(evaluationMapper.findByEvaluatedPositionAndByCompany(position, true))
                .thenReturn(Optional.of(studentEvaluation));

        Optional<StudentEvaluation> result =
                committeeService.retrieveStudentEvaluationByCompanyForPosition(position);

        assertTrue(result.isPresent());
        assertEquals(studentEvaluation, result.get());
    }

    @Test
    void testRetrieveStudentEvaluationByProfessorForPosition() {
        when(evaluationMapper.findByEvaluatedPositionAndByCompany(position, false))
                .thenReturn(Optional.of(studentEvaluation));

        Optional<StudentEvaluation> result =
                committeeService.retrieveStudentEvaluationByProfessorForPosition(position);

        assertTrue(result.isPresent());
        assertEquals(studentEvaluation, result.get());
    }

    @Test
    void testRetrieveCompanyEvaluationForPosition() {
        when(evaluationMapper.findCompanyEvaluationByEvaluatedPosition(position))
                .thenReturn(Optional.of(companyEvaluation));

        Optional<CompanyEvaluation> result =
                committeeService.retrieveCompanyEvaluationForPosition(position);

        assertTrue(result.isPresent());
        assertEquals(companyEvaluation, result.get());
    }

    @Test
    void testSaveGrade() {
        committeeService.saveGrade(position);
        verify(positionMapper).save(position);
    }
}
