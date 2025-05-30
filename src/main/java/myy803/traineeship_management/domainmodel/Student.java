package myy803.traineeship_management.domainmodel;

import java.util.ArrayList;
import java.util.List;


import jakarta.persistence.*;

@Entity
@Table(name="students")
public class Student {

	@Id
	@Column(name="username")
	private String username;
	
	@Column(name="student_name")
	private String studentName;
	
	@Column(name="am")
	private String AM;
	
	@Column(name="avg_grade")
	private double avgGrade;
	
	@Column(name="prefered_location")
	private String preferredLocation;
	
	@Column(name = "interests")
	private String interests;
	
	@Column(name = "skills")	
	private String skills;
	
	@Column(name="looking_ts")
	private boolean lookingForTraineeship;
	
	@OneToOne(mappedBy = "assignedStudent")	
	private TraineeshipPosition assignedTraineeship;
	
	public Student(String username, String studentName, String aM, double avgGrade, String preferredLocation,
			String interests, String skills, boolean lookingForTraineeship,
			TraineeshipPosition assignedTraineeship) {
		this.username = username;
		this.studentName = studentName;
		AM = aM;
		this.avgGrade = avgGrade;
		this.preferredLocation = preferredLocation;
		this.interests = interests;
		this.skills = skills;
		this.lookingForTraineeship = lookingForTraineeship;
		this.assignedTraineeship = assignedTraineeship;
	}
	
	public Student() {
		
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getStudentName() {
		return studentName;
	}
	
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	
	public String getAM() {
		return AM;
	}
	
	public void setAM(String aM) {
		AM = aM;
	}
	
	public double getAvgGrade() {
		return avgGrade;
	}
	
	public void setAvgGrade(double avgGrade) {
		this.avgGrade = avgGrade;
	}
	
	public String getPreferredLocation() {
		return preferredLocation;
	}
	
	public void setPreferredLocation(String preferredLocation) {
		this.preferredLocation = preferredLocation;
	}
	
	public String getInterests() {
		return interests;
	}
	
	public void setInterests(String interests) {
		this.interests = interests;
	}
	
	public String getSkills() {
		return skills;
	}
	
	public void setSkills(String skills) {
		this.skills = skills;
	}
	
	public boolean isLookingForTraineeship() {
		return lookingForTraineeship;
	}
	
	public void setLookingForTraineeship(boolean lookingForTraineeship) {
		this.lookingForTraineeship = lookingForTraineeship;
	}
	
	public TraineeshipPosition getAssignedTraineeship() {
		return assignedTraineeship;
	}
	
	public void setAssignedTraineeship(TraineeshipPosition assignedTraineeship) {
		this.assignedTraineeship = assignedTraineeship;
	}


	public boolean isPresent() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public String toString() {
		return studentName;
	}
	
	
}
