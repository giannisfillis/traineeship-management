package myy803.traineeship_management.controllers;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import myy803.traineeship_management.domainmodel.Student;
import myy803.traineeship_management.domainmodel.TraineeshipPosition;
import myy803.traineeship_management.domainmodel.User;
import myy803.traineeship_management.services.StudentService;
import myy803.traineeship_management.services.UserService;

@Controller
public class StudentController {
	
	@Autowired
	private StudentService studentService;

	
	
	@RequestMapping("/student/dashboard")
	public String getUserMainMenu(){
	   
		return "student/dashboard";
	}
	
	@RequestMapping("/student/save_profile")
	public String saveProfile(@ModelAttribute("student") Student student, Model model) {
		try {
			studentService.save(student);
		} catch (DataIntegrityViolationException e) {
		    model.addAttribute("dbError", "Field exceeds allowed length");
		    return "student/profile";
		}
		
		return "redirect:/student/dashboard";
	}
	
	@RequestMapping("/student/profile")
	public String retrieveProfile(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String username = authentication.getName();
		Student student = studentService.findByUsername(username);
		if(studentService.isRegistered(student)) {
			model.addAttribute("student",student);
		}
		else {
			Student newStudent = new Student();
			newStudent.setUsername(username);
			model.addAttribute("student",newStudent);
		}
		return "student/profile";
	}

	@RequestMapping("/student/apply_for_position")
	public String applyForTraineeshipPosition(RedirectAttributes redirectAttributes) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String username = authentication.getName();
		Student student = studentService.findByUsername(username);
		if (studentService.isRegistered(student)) {
			if (student.isLookingForTraineeship() == false) {
				List<TraineeshipPosition> positions = studentService.retrievePositions();
				for (TraineeshipPosition position : positions) {
					if (position.getAssignedStudent() == student){
						redirectAttributes.addFlashAttribute("error", "You are already assigned to a traineeship.");
						return "redirect:/student/dashboard";
					}
				}
				
				student.setLookingForTraineeship(true); 
				studentService.save(student);
			    redirectAttributes.addFlashAttribute("success", "You are now marked as looking for a traineeship.");
			}
			else {
				redirectAttributes.addFlashAttribute("error", "You have already applied for a traineeship.");
			}
		}
		else{
			redirectAttributes.addFlashAttribute("error", "Please complete your profile before expressing interest.");
		}
		return "redirect:/student/dashboard";
	}
	
	@RequestMapping("/student/logbook")
	public String fillLogBook(RedirectAttributes redirectAttributes, Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String username = authentication.getName();
		Student student = studentService.findByUsername(username);
		TraineeshipPosition position = studentService.findPosition(student);
		
		if (position == null) {
			redirectAttributes.addFlashAttribute("error", "You aren't assigned in a Traineeship yet");
			return "redirect:/student/dashboard";
		}
		else {
			model.addAttribute("position", position);
			return "student/logbook";
		}
	}
	
	@RequestMapping("/student/save_logbook")
	public String saveLogBook(@ModelAttribute("position") TraineeshipPosition incomingPosition,@RequestParam("newEntry") String newEntry, Model model) {
		try {
			TraineeshipPosition position = studentService.findPositionById(incomingPosition.getId());
			String current = position.getStudentLogBook() != null ? position.getStudentLogBook() : "";
			String logToAdd = position.getStudentLogBook() != null ? "," + newEntry : newEntry;
			position.setStudentLogBook(current + logToAdd);
			studentService.saveLog(position);
		} catch (DataIntegrityViolationException e) {
		    model.addAttribute("dbError", "Field exceeds allowed length");
		    return "student/logbook";
		}
		
		return "redirect:/student/dashboard";
		
	}
	
	@RequestMapping("/student/erase_logbook")
	public String eraseLogBook(@ModelAttribute("position") TraineeshipPosition incomingPosition, Model model) {
		TraineeshipPosition position = studentService.findPositionById(incomingPosition.getId());
		position.setStudentLogBook(null);
		studentService.saveLog(position);
		
		return "redirect:/student/dashboard";
	}
	
	
}