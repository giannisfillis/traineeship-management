package myy803.traineeship_management.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import myy803.traineeship_management.domainmodel.Company;
import myy803.traineeship_management.domainmodel.CompanyEvaluation;
import myy803.traineeship_management.domainmodel.Professor;
import myy803.traineeship_management.domainmodel.Student;
import myy803.traineeship_management.domainmodel.StudentEvaluation;
import myy803.traineeship_management.domainmodel.TraineeshipPosition;
import myy803.traineeship_management.domainmodel.pos_search_strategies.PositionSearchFactory;
import myy803.traineeship_management.domainmodel.pos_search_strategies.PositionSearchStrategy;
import myy803.traineeship_management.domainmodel.super_assign_strategies.SupervisorAssignmentFactory;
import myy803.traineeship_management.domainmodel.super_assign_strategies.SupervisorAssignmentStrategy;
import myy803.traineeship_management.services.CommitteeService;
import myy803.traineeship_management.services.CompanyService;
import myy803.traineeship_management.services.ProfessorService;
import myy803.traineeship_management.services.StudentService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;

@WebMvcTest(CommitteeController.class)
@AutoConfigureMockMvc(addFilters = false)
class CommitteeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommitteeService committeeService;

    @MockBean
    private StudentService studentService;

    @MockBean
    private PositionSearchFactory positionSearchFactory;

    @MockBean
    private SupervisorAssignmentFactory supervisorAssignmentFactory;

    @Test
    void testGetUserMainMenuReturnsPage() throws Exception {
        mockMvc.perform(get("/committee/dashboard"))
               .andExpect(status().isOk())
               .andExpect(view().name("committee/dashboard"));
    }

    @Test
    void testShowListOfAppliedStudentsReturnsPage() throws Exception {
        List<Student> students = List.of(new Student());
        Mockito.when(committeeService.findAppliedStudents()).thenReturn(students);

        mockMvc.perform(get("/committee/applied_students"))
               .andExpect(status().isOk())
               .andExpect(model().attribute("students",students))
               .andExpect(view().name("committee/applied_students"));
    }

    @Test
    void testShowListOfInProgressPositionsReturnsPage() throws Exception {
        List<TraineeshipPosition> positions = List.of(new TraineeshipPosition());
        Mockito.when(committeeService.findInProgressPositions()).thenReturn(positions);

        mockMvc.perform(get("/committee/inprogress_positions"))
               .andExpect(status().isOk())
               .andExpect(model().attribute("positions",positions))
               .andExpect(view().name("committee/inprogress_positions"));
    }

    @Test
    void testChooseMethodFormReturnsPage() throws Exception {
        Student student = new Student();
        Mockito.when(studentService.findByUsername("testStudent")).thenReturn(student);

        mockMvc.perform(get("/committee/choose_method")
                .param("selected_student_username", "testStudent"))
               .andExpect(status().isOk())
               .andExpect(model().attribute("student", student))
               .andExpect(view().name("committee/choose_method"));
    }

    @Test
    void testShowAvailablePositionsReturnsPage() throws Exception {
        PositionSearchStrategy strategy = Mockito.mock(PositionSearchStrategy.class);
        List<TraineeshipPosition> positions = List.of(new TraineeshipPosition());
        Mockito.when(positionSearchFactory.create("testStrategy")).thenReturn(strategy);
        Mockito.when(strategy.search("testStudent")).thenReturn(positions);

        mockMvc.perform(get("/committee/available_positions")
                .param("selected_student_username", "testStudent")
                .param("selectedStrategy", "testStrategy"))
               .andExpect(status().isOk())
               .andExpect(model().attribute("positions", positions))
               .andExpect(model().attribute("studentUsername", "testStudent"))
               .andExpect(view().name("committee/available_positions"));
    }

    @Test
    void testAssignPositionReturnsPage() throws Exception {
        mockMvc.perform(get("/committee/assign_position")
                .param("studentUsername", "testStudent")
                .param("positionId", "100"))
               .andExpect(status().isFound())
               .andExpect(view().name("redirect:/committee/dashboard"));

        Mockito.verify(committeeService).assignPositionToStudent(100, "testStudent");
    }

    @Test
    void testChooseSupervisorAssignmentMethodAlreadyAssigned() throws Exception {
        TraineeshipPosition position = new TraineeshipPosition();
        position.setId(100);
        position.setSupervisor(new Professor());
        Mockito.when(committeeService.findPosition(100)).thenReturn(position);

        mockMvc.perform(get("/committee/choose_supervisor").param("selected_position", "100"))
               .andExpect(status().isFound())
               .andExpect(flash().attributeExists("error"))
               .andExpect(view().name("redirect:/committee/inprogress_positions"));
    }

    @Test
    void testChooseSupervisorAssignmentMethodNotAssigned() throws Exception {
        TraineeshipPosition position = new TraineeshipPosition();
        position.setId(100);
        Mockito.when(committeeService.findPosition(100)).thenReturn(position);

        mockMvc.perform(get("/committee/choose_supervisor").param("selected_position", "100"))
               .andExpect(status().isOk())
               .andExpect(model().attribute("position", position))
               .andExpect(view().name("committee/choose_supervisor"));
    }

    @Test
    void testAssignSupervisor() throws Exception {
        TraineeshipPosition position = new TraineeshipPosition();
        position.setId(100);
        position.setTitle("Test Title");

        Professor professor = new Professor();
        professor.setProfessorName("testProf");

        Mockito.when(committeeService.findPosition(100)).thenReturn(position);

        SupervisorAssignmentStrategy strategy = Mockito.mock(SupervisorAssignmentStrategy.class);
        Mockito.when(supervisorAssignmentFactory.create("testStrategy")).thenReturn(strategy);
        Mockito.when(strategy.assign(100)).thenReturn(professor);

        mockMvc.perform(get("/committee/assign_supervisor")
                .param("selected_position", "100")
                .param("selectedStrategy", "testStrategy"))
               .andExpect(status().isFound())
               .andExpect(flash().attributeExists("successMessage"))
               .andExpect(view().name("redirect:/committee/inprogress_positions"));

        Mockito.verify(committeeService).assignSuperivisorToPosition(100, professor);
    }

    @Test
    void testGradeTraineeshipPositionEvaluationsPending() throws Exception {
        TraineeshipPosition position = new TraineeshipPosition();
        position.setId(100);
        Mockito.when(committeeService.findPosition(100)).thenReturn(position);
        Mockito.when(committeeService.retrieveStudentEvaluationByCompanyForPosition(position)).thenReturn(Optional.empty());

        mockMvc.perform(get("/committee/grade_position").param("selected_position", "100"))
               .andExpect(status().isFound())
               .andExpect(flash().attributeExists("error"))
               .andExpect(view().name("redirect:/committee/inprogress_positions"));
    }

    @Test
    void testGradeTraineeshipPositionEvaluationsPresent() throws Exception {
        TraineeshipPosition position = new TraineeshipPosition();
        position.setId(100);
        StudentEvaluation s1 = new StudentEvaluation();
        StudentEvaluation s2 = new StudentEvaluation();
        CompanyEvaluation c1 = new CompanyEvaluation();

        Mockito.when(committeeService.findPosition(100)).thenReturn(position);
        Mockito.when(committeeService.retrieveStudentEvaluationByCompanyForPosition(position)).thenReturn(Optional.of(s1));
        Mockito.when(committeeService.retrieveStudentEvaluationByProfessorForPosition(position)).thenReturn(Optional.of(s2));
        Mockito.when(committeeService.retrieveCompanyEvaluationForPosition(position)).thenReturn(Optional.of(c1));

        mockMvc.perform(get("/committee/grade_position").param("selected_position", "100"))
               .andExpect(status().isOk())
               .andExpect(view().name("committee/grade_position"))
               .andExpect(model().attributeExists(
                       "student_evaluation_bycompany",
                       "student_evaluation_byprofessor",
                       "company_evaluation",
                       "position"));
    }

    @Test
    void testSaveGradeOfTraineeshipPositionReturnsPage() throws Exception {
        TraineeshipPosition expected = new TraineeshipPosition();
        expected.setId(100);
        expected.setPassFailGrade(true);

        TraineeshipPosition real = new TraineeshipPosition();
        real.setId(100);

        Mockito.when(committeeService.findPosition(100)).thenReturn(real);

        mockMvc.perform(post("/committee/save_grade")
                .flashAttr("position", expected))
               .andExpect(status().isFound())
               .andExpect(view().name("redirect:/committee/inprogress_positions"));

        Mockito.verify(committeeService).saveGrade(real);
        Assertions.assertEquals(true, real.getPassFailGrade());
    }
}

