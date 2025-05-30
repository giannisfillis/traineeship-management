package myy803.traineeship_management.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

import myy803.traineeship_management.domainmodel.Student;
import myy803.traineeship_management.domainmodel.TraineeshipPosition;
import myy803.traineeship_management.services.StudentService;

@WebMvcTest(StudentController.class)
@AutoConfigureMockMvc(addFilters = false)
class StudentControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentController studentController;

    private Student mockStudent;

 
    
    @TestConfiguration
    static class TestConfig {
        @Bean
        public StudentService studentService() {
            return Mockito.mock(StudentService.class);
        }
    }
    
    @BeforeEach
    public void setup() {
		mockMvc = MockMvcBuilders
          .webAppContextSetup(context)
          .build();
		
		Mockito.reset(studentService);
		mockStudent = new Student();
		mockStudent.setUsername("testStudent");
    }
	
    
    @Test
    void testGetUserMainMenuReturnsPage() throws Exception {
        mockMvc.perform(get("/student/dashboard"))
               .andExpect(status().isOk())
               .andExpect(view().name("student/dashboard"));
    }

    @Test
    void testSaveProfileReturnsPage() throws Exception {
        mockMvc.perform(post("/student/save_profile")
                .flashAttr("student", mockStudent))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/student/dashboard"));

        verify(studentService).save(any(Student.class));
    }
    
    @WithMockUser(username = "testStudent", roles = {"STUDENT"})
    @Test
    void testRetrieveProfileWhenRegistered() throws Exception {
        when(studentService.findByUsername("testStudent")).thenReturn(mockStudent);
        when(studentService.isRegistered(mockStudent)).thenReturn(true);

        mockMvc.perform(get("/student/profile"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("student", mockStudent))
                .andExpect(view().name("student/profile"));
    }
    
    @WithMockUser(username = "testStudent", roles = {"STUDENT"})
    @Test
    void testRetrieveProfileWhenNotRegistered() throws Exception {
        when(studentService.findByUsername("testStudent")).thenReturn(mockStudent);
        when(studentService.isRegistered(mockStudent)).thenReturn(false);

        mockMvc.perform(get("/student/profile"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("student"))
                .andExpect(view().name("student/profile"));
    }
    
    @WithMockUser(username = "testStudent", roles = {"STUDENT"})
    @Test
    void testApplyForTraineeshipReturnsPage() throws Exception {
        mockStudent.setLookingForTraineeship(false);
        when(studentService.findByUsername("testStudent")).thenReturn(mockStudent);
        when(studentService.isRegistered(mockStudent)).thenReturn(true);

        mockMvc.perform(get("/student/apply_for_position"))
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:/student/dashboard"));

        verify(studentService).save(mockStudent);
    }
    
    @WithMockUser(username = "testStudent", roles = {"STUDENT"})
    @Test
    void testApplyForTraineeshipAlreadyApplied() throws Exception {
        mockStudent.setLookingForTraineeship(true);
        when(studentService.findByUsername("testStudent")).thenReturn(mockStudent);
        when(studentService.isRegistered(mockStudent)).thenReturn(true);

        mockMvc.perform(get("/student/apply_for_position"))
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:/student/dashboard"));

        verify(studentService, never()).save(mockStudent);
    }
    
    @WithMockUser(username = "testStudent", roles = {"STUDENT"})
    @Test
    void testFillLogBookReturnsPage() throws Exception {
        TraineeshipPosition position = new TraineeshipPosition();
        when(studentService.findByUsername("testStudent")).thenReturn(mockStudent);
        when(studentService.findPosition(mockStudent)).thenReturn(position);

        mockMvc.perform(get("/student/logbook"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("position", position))
                .andExpect(view().name("student/logbook"));
    }
    
    @WithMockUser(username = "testStudent", roles = {"STUDENT"})
    @Test
    void testFillLogBookWithNoPosition() throws Exception {
        when(studentService.findByUsername(anyString())).thenReturn(mockStudent);
        when(studentService.findPosition(mockStudent)).thenReturn(null);

        mockMvc.perform(get("/student/logbook"))
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:/student/dashboard"));
    }
    
    @Test
    void testSaveLogBook() throws Exception {
        TraineeshipPosition pos = new TraineeshipPosition();
        pos.setId(100);
        pos.setStudentLogBook("test_log");

        when(studentService.findPositionById(100)).thenReturn(pos);

        mockMvc.perform(post("/student/save_logbook")
                .param("newEntry", "newLog")
                .param("id","100"))
        		.andExpect(status().isFound())
                .andExpect(view().name("redirect:/student/dashboard"));

        verify(studentService).saveLog(pos);
    }

    

    @Test
    void testEraseLogBook() throws Exception {
        TraineeshipPosition pos = new TraineeshipPosition();
        pos.setId(100);
        when(studentService.findPositionById(100)).thenReturn(pos);

        mockMvc.perform(post("/student/erase_logbook")
        		.param("id","100"))
        		.andExpect(status().isFound())
        		.andExpect(view().name("redirect:/student/dashboard"));

        verify(studentService).saveLog(pos);
    }
}
