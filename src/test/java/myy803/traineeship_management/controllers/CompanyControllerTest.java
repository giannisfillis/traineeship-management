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

import myy803.traineeship_management.domainmodel.Company;
import myy803.traineeship_management.domainmodel.StudentEvaluation;
import myy803.traineeship_management.domainmodel.TraineeshipPosition;
import myy803.traineeship_management.services.CompanyService;
import myy803.traineeship_management.services.ProfessorService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;

@WebMvcTest(CompanyController.class)
@AutoConfigureMockMvc(addFilters = false)
class CompanyControllerTest {

	@Autowired
    private WebApplicationContext context;
	
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CompanyService companyService;

    private Company mockCompany;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public CompanyService companyService() {
            return Mockito.mock(CompanyService.class);
        }
    }
    
    @BeforeEach
    void setUp() {
    	mockMvc = MockMvcBuilders
  	          .webAppContextSetup(context)
  	          .build();
    	
    	Mockito.reset(companyService);
        mockCompany = new Company();
        mockCompany.setUsername("testCompany");
    }

    @Test
    void testDashboardReturnsPage() throws Exception {
        mockMvc.perform(get("/company/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("company/dashboard"));
    }

    @Test
    void testSaveProfileReturnsPage() throws Exception {
        mockMvc.perform(post("/company/save_profile")
                .flashAttr("company", mockCompany))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/company/dashboard"));

        verify(companyService).save(mockCompany);
    }

    @WithMockUser(username = "testCompany", roles = {"COMPANY"})
    @Test
    void testRetrieveProfileWhenRegistered() throws Exception {
        when(companyService.findByUsername("testCompany")).thenReturn(mockCompany);
        when(companyService.isRegistered(mockCompany)).thenReturn(true);

        mockMvc.perform(get("/company/profile"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("company", mockCompany))
                .andExpect(view().name("company/profile"));
    }

    @WithMockUser(username = "testCompany", roles = {"COMPANY"})
    @Test
    void testRetrieveProfileWhenNotRegistered() throws Exception {
        when(companyService.findByUsername("testCompany")).thenReturn(mockCompany);
        when(companyService.isRegistered(mockCompany)).thenReturn(false);

        mockMvc.perform(get("/company/profile"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("company"))
                .andExpect(view().name("company/profile"));
    }

    @WithMockUser(username = "testCompany", roles = {"COMPANY"})
    @Test
    void testListOfferedPositionsWhenRegistered() throws Exception {
    	List<TraineeshipPosition> positions = List.of(new TraineeshipPosition());
    	
        when(companyService.findByUsername("testCompany")).thenReturn(mockCompany);
        when(companyService.isRegistered(mockCompany)).thenReturn(true);
        when(companyService.findTraineeshipPositions(mockCompany)).thenReturn(positions);

        mockMvc.perform(get("/company/offered_positions"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("offers", positions))
                .andExpect(view().name("company/offered_positions"));
    }

    @WithMockUser(username = "testCompany", roles = {"COMPANY"})
    @Test
    void testListOfferedPositionsWhenNotRegistered() throws Exception {
        when(companyService.findByUsername("testCompany")).thenReturn(mockCompany);
        when(companyService.isRegistered(mockCompany)).thenReturn(false);

        mockMvc.perform(get("/company/offered_positions"))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/company/dashboard"))
                .andExpect(flash().attributeExists("error"));
    }

    @WithMockUser(username = "testCompany", roles = {"COMPANY"})
    @Test
    void testListAssignedPositionsReturnsPage() throws Exception {
    	List<TraineeshipPosition> positions = List.of(new TraineeshipPosition());
        when(companyService.findByUsername("testCompany")).thenReturn(mockCompany);
        when(companyService.findAssignedTraineeshipPositions(mockCompany)).thenReturn(positions);

        mockMvc.perform(get("/company/assigned_positions"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("assignedPositions", positions))
                .andExpect(view().name("company/assigned_positions"));
    }

    @Test
    void testAdvertisePositionReturnsPage() throws Exception {
    	
        mockMvc.perform(get("/company/advertise_position"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("position"))
                .andExpect(view().name("company/advertise_position"));
    }

    @WithMockUser(username = "testCompany", roles = {"COMPANY"})
    @Test
    void testSavePositionAdvertisementReturnsPage() throws Exception {
        TraineeshipPosition position = new TraineeshipPosition();
        when(companyService.findByUsername("testCompany")).thenReturn(mockCompany);

        mockMvc.perform(post("/company/savePosition")
                .flashAttr("assignedPositions", position))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/company/dashboard"));

        verify(companyService).savePosition(any());
    }

    
    @Test
    void testDeletePositionAdvertisementReturnsPage() throws Exception {
        mockMvc.perform(post("/company/delete_position_offer")
                .param("selected_position_id", "100"))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/company/dashboard"));

        verify(companyService).deleteSpecificPosition(100);
    }

    @Test
    void testEvaluatePositionWhenNotAlreadyEvaluated() throws Exception {
        TraineeshipPosition position = new TraineeshipPosition();
        position.setId(100);
        when(companyService.retrievePositionForEvaluation(100)).thenReturn(position);
        when(companyService.findStudentEvaluationByPosition(position)).thenReturn(Optional.empty());

        mockMvc.perform(get("/company/evaluate_position").param("selected_position_id", "100"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("position", "evaluation"))
                .andExpect(view().name("company/evaluate_position"));
    }

    @Test
    void testEvaluatePositionAlreadyEvaluated() throws Exception {
        TraineeshipPosition position = new TraineeshipPosition();
        position.setId(100);
        when(companyService.retrievePositionForEvaluation(100)).thenReturn(position);
        when(companyService.findStudentEvaluationByPosition(position))
                .thenReturn(Optional.of(new StudentEvaluation()));

        mockMvc.perform(get("/company/evaluate_position").param("selected_position_id", "100"))
                .andExpect(status().isFound())
                .andExpect(flash().attributeExists("error"))
                .andExpect(view().name("redirect:/company/assigned_positions"));
    }

    @Test
    void testSaveEvaluationReturnsPage() throws Exception {
        TraineeshipPosition position = new TraineeshipPosition();
        position.setId(100);
        when(companyService.retrievePositionForEvaluation(100)).thenReturn(position);

        mockMvc.perform(post("/company/save_evaluation")
                .param("selected_position_id", "100")
                .flashAttr("evaluation", new StudentEvaluation()))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/company/dashboard"));

        verify(companyService).saveTraineeshipEvaluation(eq(position), any(StudentEvaluation.class));
    }
}
