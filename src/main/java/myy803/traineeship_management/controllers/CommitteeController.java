package myy803.traineeship_management.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
import myy803.traineeship_management.services.StudentService;

@Controller
public class CommitteeController {

	@Autowired
	private CommitteeService committeeService;
	
	@Autowired
	private StudentService studentService;
	
	@Autowired 
	private PositionSearchFactory positionSearchFactory;
	
	@Autowired
	private SupervisorAssignmentFactory supervisorAssignmentFactory;

	@RequestMapping("/committee/dashboard")
	public String getUserMainMenu(){
	   
		return "committee/dashboard";
	}
	
	@RequestMapping("/committee/applied_students")
	public String showListOfAppliedStudents(Model model) {
		
		List<Student> appliedStudents = committeeService.findAppliedStudents();
		model.addAttribute("students", appliedStudents);
		
		return "committee/applied_students";
	}
	
	@RequestMapping("/committee/inprogress_positions")
	public String showListOfInProgressPositions(Model model) {
		
		List<TraineeshipPosition> positions = committeeService.findInProgressPositions();
		model.addAttribute("positions", positions);
		
		return "committee/inprogress_positions";
	}
	
	@RequestMapping("/committee/choose_method")
	public String chooseMethodForm(@RequestParam("selected_student_username") String username,Model model){
		 Student student = studentService.findByUsername(username);
		 model.addAttribute("student",student);
		 
		return "committee/choose_method";
	}
	
	@RequestMapping("/committee/available_positions")
	public String showAvailablePositions(@RequestParam("selected_student_username") String studentUsername,@RequestParam("selectedStrategy") String selectedStrategy,
	                                 Model model) {

	    PositionSearchStrategy chosenStrategy = positionSearchFactory.create(selectedStrategy);
	    List<TraineeshipPosition> positions = chosenStrategy.search(studentUsername);
	    model.addAttribute("studentUsername",studentUsername);
	    model.addAttribute("positions", positions);
	    return "committee/available_positions";
	}
	
	@RequestMapping("/committee/assign_position")
	public String assignPosition(@RequestParam("studentUsername") String studentusername,@RequestParam("positionId") int posID,Model model) {
		committeeService.assignPositionToStudent(posID,studentusername);
		return "redirect:/committee/dashboard";
	}
	
	@RequestMapping("/committee/choose_supervisor")
	public String chooseSupervisorAssignmentMethod(RedirectAttributes redirectAttributes, @RequestParam("selected_position") int positionID, Model model) {
		TraineeshipPosition selectedPosition = committeeService.findPosition(positionID);
		if (selectedPosition.getSupervisor() != null) {
			redirectAttributes.addFlashAttribute("error", "This position has an assigned supervisor");
			return "redirect:/committee/inprogress_positions";
		}
		
		model.addAttribute("position", selectedPosition);
		return "committee/choose_supervisor";
	}
	
	@RequestMapping("/committee/assign_supervisor")
	public String assignSupervisor(RedirectAttributes redirectAttributes, @RequestParam("selected_position") int positionID, @RequestParam("selectedStrategy") String selectedStrategy, Model model) {
		TraineeshipPosition position = committeeService.findPosition(positionID);
		
		SupervisorAssignmentStrategy strategy = supervisorAssignmentFactory.create(selectedStrategy);
		Professor professor = strategy.assign(positionID);
		committeeService.assignSuperivisorToPosition(positionID,professor);
		
		redirectAttributes.addFlashAttribute("successMessage", 
			    "Supervisor " + professor.getProfessorName() + " was assigned to position '" + position.getTitle() + "'.");
		
		return "redirect:/committee/inprogress_positions";
	}
	
	@RequestMapping("/committee/grade_position")
	public String gradeTraineeshipPosition(RedirectAttributes redirectAttributes, @RequestParam("selected_position") int posId, Model model) {
		
		TraineeshipPosition position = committeeService.findPosition(posId);
		model.addAttribute("position", position);
		Optional<StudentEvaluation> studentEvaluationByCompany = committeeService.retrieveStudentEvaluationByCompanyForPosition(position);
		Optional<StudentEvaluation> studentEvaluationByProfessor = committeeService.retrieveStudentEvaluationByProfessorForPosition(position);
		Optional<CompanyEvaluation> companyEvaluation = committeeService.retrieveCompanyEvaluationForPosition(position);
		if (studentEvaluationByCompany.isEmpty() || studentEvaluationByProfessor.isEmpty() || companyEvaluation.isEmpty()) {
			redirectAttributes.addFlashAttribute("error", "Evaluations in this position are still pending");
			return "redirect:/committee/inprogress_positions";
		}
		
		model.addAttribute("student_evaluation_bycompany", studentEvaluationByCompany.get());
		model.addAttribute("student_evaluation_byprofessor", studentEvaluationByProfessor.get());
		model.addAttribute("company_evaluation", companyEvaluation.get());
		
		return "committee/grade_position";
	}
	
	@RequestMapping("/committee/save_grade")
	public String saveGradeOfTraineeshipPosition(@ModelAttribute("position") TraineeshipPosition incomingPosition) {
		
		TraineeshipPosition position = committeeService.findPosition(incomingPosition.getId());
		position.setPassFailGrade(incomingPosition.getPassFailGrade());
		committeeService.saveGrade(position);
		
		return "redirect:/committee/inprogress_positions";
	}
	
	
}