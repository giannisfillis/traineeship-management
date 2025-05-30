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
import myy803.traineeship_management.domainmodel.Evaluation;
import myy803.traineeship_management.domainmodel.Student;
import myy803.traineeship_management.domainmodel.StudentEvaluation;
import myy803.traineeship_management.domainmodel.TraineeshipPosition;
import myy803.traineeship_management.services.CompanyService;

@Controller
public class CompanyController {
	
	@Autowired
	private CompanyService companyService;
	

	
	@RequestMapping("/company/dashboard")
	public String getUserMainMenu(){
	   
		return "company/dashboard";
	}
	
	@RequestMapping("/company/save_profile")
	public String saveProfile(@ModelAttribute("company") Company company, Model model) {
		try {
			companyService.save(company);
		} catch (DataIntegrityViolationException e) {
		    model.addAttribute("dbError", "Field exceeds allowed length");
		    return "company/profile";
		}
		
		return "redirect:/company/dashboard";
	}
	
	@RequestMapping("/company/profile")
	public String retrieveProfile(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String username = authentication.getName();
		Company company = companyService.findByUsername(username);
		if(companyService.isRegistered(company)) {
			model.addAttribute("company",company);
		}
		else {
			Company newCompany = new Company();
			newCompany.setUsername(username);
			model.addAttribute("company",newCompany);
		}
		return "company/profile";
	}

	@RequestMapping("/company/offered_positions")
	public String listOfferedPositions(Model model,RedirectAttributes redirectAttributes) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String username = authentication.getName();
	    Company company = companyService.findByUsername(username);
	    if (companyService.isRegistered(company)) {
	    	List<TraineeshipPosition> traineeshipPositions = companyService.findTraineeshipPositions(company);	    
	    	model.addAttribute("offers", traineeshipPositions);
	    }
	    else {
	    	redirectAttributes.addFlashAttribute("error", "Please complete your profile before continuing");
			return "redirect:/company/dashboard";
	    }
		
		return "company/offered_positions";
	}
	
	@RequestMapping("/company/assigned_positions")
	public String listAssignedPositions(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String username = authentication.getName();
		Company company = companyService.findByUsername(username);
	    List<TraineeshipPosition> assignedTraineeshipPositions = companyService.findAssignedTraineeshipPositions(company);
	    
	    model.addAttribute("assignedPositions", assignedTraineeshipPositions);
		
		return "company/assigned_positions";
	}
	
	@RequestMapping("/company/advertise_position")
	public String advertisePosition(Model model) {
		TraineeshipPosition traineeshipPosition = new TraineeshipPosition();
	    model.addAttribute("position", traineeshipPosition);
		
		return "company/advertise_position";
	}
	
	@RequestMapping("/company/savePosition") 
	public String savePositionAdvertisement(@ModelAttribute("assignedPositions") TraineeshipPosition traineeshipPosition, Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String username = authentication.getName();
		Company company = companyService.findByUsername(username);
		traineeshipPosition.setOfferingCompany(company);
		
		try {
			companyService.savePosition(traineeshipPosition);
		} catch (DataIntegrityViolationException e) {
		    model.addAttribute("dbError", "Field exceeds allowed length");
		    return "company/advertise_position";
		}
		return "redirect:/company/dashboard";
	}
	
	@RequestMapping("/company/delete_position_offer")
	public String deletePositionAdvertisement(@RequestParam("selected_position_id") int posId) {
		companyService.deleteSpecificPosition(posId);
		return "redirect:/company/dashboard";
	}
	
	@RequestMapping("/company/evaluate_position")
	public String evaluatePosition(RedirectAttributes redirectAttributes, @RequestParam("selected_position_id") int posId, Model model ) {
		TraineeshipPosition position = companyService.retrievePositionForEvaluation(posId);
		model.addAttribute("position", position);

		Optional<StudentEvaluation> existingEvaluation = companyService.findStudentEvaluationByPosition(position);
		if (existingEvaluation.isPresent()) {
			redirectAttributes.addFlashAttribute("error", "This position has been already evaluated");
			return "redirect:/company/assigned_positions";
		}
		
		Evaluation evaluation = new StudentEvaluation();
		model.addAttribute("evaluation", evaluation);
		
		return "company/evaluate_position";
	}
	
	@RequestMapping("/company/save_evaluation")
	public String saveEvaluation(@RequestParam("selected_position_id") int posId, @ModelAttribute("evaluation") StudentEvaluation evaluation, Model model) {
		TraineeshipPosition position = companyService.retrievePositionForEvaluation(posId);
		companyService.saveTraineeshipEvaluation(position, evaluation);
		
		return "redirect:/company/dashboard";
	}
	
	
}
