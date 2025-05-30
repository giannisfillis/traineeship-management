package myy803.traineeship_management.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import myy803.traineeship_management.domainmodel.Company;
import myy803.traineeship_management.domainmodel.CompanyEvaluation;
import myy803.traineeship_management.domainmodel.Evaluation;
import myy803.traineeship_management.domainmodel.Professor;
import myy803.traineeship_management.domainmodel.StudentEvaluation;
import myy803.traineeship_management.domainmodel.TraineeshipPosition;
import myy803.traineeship_management.services.ProfessorService;

@Controller
public class ProfessorController {
	
	@Autowired
	private ProfessorService professorService;

	@RequestMapping("/professor/dashboard")
	public String getUserMainMenu(){
	   
		return "professor/dashboard";
	}
	
	@RequestMapping("/professor/save_profile")
	public String saveProfile(@ModelAttribute("professor") Professor professor, Model model) {
		try {
			professorService.save(professor);
		} catch (DataIntegrityViolationException e) {
		    model.addAttribute("dbError", "Field exceeds allowed length");
		    return "professor/profile";
		}
		
		return "redirect:/professor/dashboard";
	}
	
	@RequestMapping("/professor/profile")
	public String retrieveProfile(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String username = authentication.getName();
		Professor professor = professorService.findByUsername(username);
		if(professorService.isRegistered(professor)) {
			model.addAttribute("professor",professor);
		}
		else {
			Professor newProfessor = new Professor();
			newProfessor.setUsername(username);
			model.addAttribute("professor",newProfessor);
		}
		return "professor/profile";
	}
	
	@RequestMapping("/professor/positions")
	public String showSupervisingPositions(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String username = authentication.getName();
		Professor professor = professorService.findByUsername(username);
		List<TraineeshipPosition> positions = professorService.retrievePositions(professor);
		
		model.addAttribute("positions", positions);
		return "professor/positions";
	}
	
	@RequestMapping("/professor/evaluate_student")
	public String evaluateStudent(RedirectAttributes redirectAttributes, @RequestParam("selected_position_id") int posId, Model model ) {
		TraineeshipPosition position = professorService.retrievePositionForEvaluation(posId);
		model.addAttribute("position", position);

		Optional<StudentEvaluation> existingStudentEvaluation = professorService.findStudentEvaluationByPosition(position);
		if (existingStudentEvaluation.isPresent()) {
			redirectAttributes.addFlashAttribute("error", "The student in this position has been already evaluated");
			return "redirect:/professor/positions";
		}
		
		Evaluation studentEvaluation = new StudentEvaluation();
		model.addAttribute("evaluation", studentEvaluation);
		
		return "professor/evaluate_student";
	}
	
	@RequestMapping("/professor/save_student_evaluation")
	public String saveStudentEvaluation(@RequestParam("selected_position_id") int posId, @ModelAttribute("evaluation") StudentEvaluation studentEvaluation, Model model) {
		TraineeshipPosition position = professorService.retrievePositionForEvaluation(posId);
		professorService.saveStudentEvaluation(position, studentEvaluation);
		
		return "redirect:/professor/positions";
	}
	
	@RequestMapping("/professor/evaluate_company")
	public String evaluateCompany(RedirectAttributes redirectAttributes, @RequestParam("selected_position_id") int posId, Model model ) {
		TraineeshipPosition position = professorService.retrievePositionForEvaluation(posId);
		model.addAttribute("position", position);

		Optional<CompanyEvaluation> existingCopmanyEvaluation = professorService.findCompanyEvaluationByPosition(position);
		if (existingCopmanyEvaluation.isPresent()) {
			redirectAttributes.addFlashAttribute("error", "The hosting company in this position has been already evaluated");
			return "redirect:/professor/positions";
		}
		
		Evaluation companyEvaluation = new CompanyEvaluation();
		model.addAttribute("evaluation", companyEvaluation);
		
		return "professor/evaluate_company";
	}
	
	@RequestMapping("/professor/save_company_evaluation")
	public String saveCompanyEvaluation(@RequestParam("selected_position_id") int posId, @ModelAttribute("evaluation") CompanyEvaluation companyEvaluation, Model model) {
		TraineeshipPosition position = professorService.retrievePositionForEvaluation(posId);
		professorService.saveCompanyEvaluation(position, companyEvaluation);
		
		return "redirect:/professor/positions";
	}
	
}
