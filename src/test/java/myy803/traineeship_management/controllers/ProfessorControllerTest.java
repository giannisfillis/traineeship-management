package myy803.traineeship_management.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;

import myy803.traineeship_management.domainmodel.CompanyEvaluation;
import myy803.traineeship_management.domainmodel.Professor;
import myy803.traineeship_management.domainmodel.Student;
import myy803.traineeship_management.domainmodel.StudentEvaluation;
import myy803.traineeship_management.domainmodel.TraineeshipPosition;
import myy803.traineeship_management.services.ProfessorService;
import myy803.traineeship_management.services.StudentService;

@WebMvcTest(ProfessorController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProfessorControllerTest {

	@Autowired
    private WebApplicationContext context;
	
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProfessorService professorService;

    private Professor mockProfessor;
    private TraineeshipPosition mockPosition;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ProfessorService professorService() {
            return Mockito.mock(ProfessorService.class);
        }
    }
    
    @BeforeEach
    void setup() {
    	mockMvc = MockMvcBuilders
    	          .webAppContextSetup(context)
    	          .build();
    			
    	Mockito.reset(professorService);
    	
        mockProfessor = new Professor();
        mockProfessor.setUsername("testProf");
        mockPosition = new TraineeshipPosition();
    }

    @Test
    void testGetUserMainMenuReturnsPage() throws Exception {
        mockMvc.perform(get("/professor/dashboard"))
               .andExpect(status().isOk())
               .andExpect(view().name("professor/dashboard"));
    }

    @Test
    void testSaveProfileReturnsPage() throws Exception {
        mockMvc.perform(post("/professor/save_profile")
                .flashAttr("professor", mockProfessor))
               .andExpect(status().isFound())
               .andExpect(view().name("redirect:/professor/dashboard"));

        verify(professorService).save(mockProfessor);
    }


    @WithMockUser(username = "testProf", roles = {"PROFESSOR"})
    @Test
    void testRetrieveProfileWhenRegistered() throws Exception {
        when(professorService.findByUsername("testProf")).thenReturn(mockProfessor);
        when(professorService.isRegistered(mockProfessor)).thenReturn(true);

        mockMvc.perform(get("/professor/profile"))
               .andExpect(status().isOk())
               .andExpect(model().attribute("professor", mockProfessor))
               .andExpect(view().name("professor/profile"));
    }

    @WithMockUser(username = "testProf", roles = {"PROFESSOR"})
    @Test
    void testRetrieveProfileWhenNotRegistered() throws Exception {
        when(professorService.findByUsername("testProf")).thenReturn(mockProfessor);
        when(professorService.isRegistered(mockProfessor)).thenReturn(false);

        mockMvc.perform(get("/professor/profile"))
               .andExpect(status().isOk())
               .andExpect(model().attributeExists("professor"))
               .andExpect(view().name("professor/profile"));
    }

    @WithMockUser(username = "testProf", roles = {"PROFESSOR"})
    @Test
    void testShowSupervisingPositionsReturnsPage() throws Exception {
        List<TraineeshipPosition> positions = List.of(new TraineeshipPosition());
        when(professorService.findByUsername("testProf")).thenReturn(mockProfessor);
        when(professorService.retrievePositions(mockProfessor)).thenReturn(positions);

        mockMvc.perform(get("/professor/positions"))
               .andExpect(status().isOk())
               .andExpect(model().attribute("positions", positions))
               .andExpect(view().name("professor/positions"));
    }

    @Test
    void testEvaluateStudentAlreadyEvaluated() throws Exception {
    	mockPosition.setId(100);
        
        when(professorService.retrievePositionForEvaluation(100)).thenReturn(mockPosition);
        when(professorService.findStudentEvaluationByPosition(mockPosition))
            .thenReturn(Optional.of(new StudentEvaluation()));

        mockMvc.perform(get("/professor/evaluate_student")
                .param("selected_position_id", "100"))
               .andExpect(status().isFound())
               .andExpect(flash().attributeExists("error"))
               .andExpect(view().name("redirect:/professor/positions"));
    }

    @Test
    void testEvaluateStudentNewEvaluation() throws Exception {
    	mockPosition.setId(100);
        
        when(professorService.retrievePositionForEvaluation(100)).thenReturn(mockPosition);
        when(professorService.findStudentEvaluationByPosition(mockPosition)).thenReturn(Optional.empty());

        mockMvc.perform(get("/professor/evaluate_student")
                .param("selected_position_id", "100"))
               .andExpect(status().isOk())
               .andExpect(model().attribute("position", mockPosition))
               .andExpect(model().attributeExists("evaluation"))
               .andExpect(view().name("professor/evaluate_student"));
    }

    @Test
    void testSaveStudentEvaluation() throws Exception {
    	mockPosition.setId(100);
        
        when(professorService.retrievePositionForEvaluation(100)).thenReturn(mockPosition);
        mockMvc.perform(post("/professor/save_student_evaluation")
                .param("selected_position_id", "100")
                .flashAttr("evaluation", new StudentEvaluation()))
               .andExpect(status().isFound())
               .andExpect(view().name("redirect:/professor/positions"));

        verify(professorService).saveStudentEvaluation(eq(mockPosition), any());
    }

    @Test
    void testEvaluateCompanyAlreadyEvaluated() throws Exception {
    	mockPosition.setId(100);
        when(professorService.retrievePositionForEvaluation(100)).thenReturn(mockPosition);
        when(professorService.findCompanyEvaluationByPosition(mockPosition)).thenReturn(Optional.of(new CompanyEvaluation()));

        mockMvc.perform(get("/professor/evaluate_company")
                .param("selected_position_id", "100"))
               .andExpect(status().isFound())
               .andExpect(flash().attributeExists("error"))
               .andExpect(view().name("redirect:/professor/positions"));
    }

    @Test
    void testEvaluateCompanyNewEvaluation() throws Exception {
    	mockPosition.setId(100);
        when(professorService.retrievePositionForEvaluation(100)).thenReturn(mockPosition);
        when(professorService.findCompanyEvaluationByPosition(mockPosition)).thenReturn(Optional.empty());

        mockMvc.perform(get("/professor/evaluate_company")
                .param("selected_position_id", "100"))
               .andExpect(status().isOk())
               .andExpect(model().attribute("position", mockPosition))
               .andExpect(model().attributeExists("evaluation"))
               .andExpect(view().name("professor/evaluate_company"));
    }

    @Test
    void testSaveCompanyEvaluation() throws Exception {
    	mockPosition.setId(100);
        when(professorService.retrievePositionForEvaluation(100)).thenReturn(mockPosition);

        mockMvc.perform(post("/professor/save_company_evaluation")
                .param("selected_position_id", "100")
                .flashAttr("evaluation", new CompanyEvaluation()))
               .andExpect(status().isFound())
               .andExpect(view().name("redirect:/professor/positions"));

        verify(professorService).saveCompanyEvaluation(eq(mockPosition), any());
    }
}