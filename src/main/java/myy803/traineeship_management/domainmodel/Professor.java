package myy803.traineeship_management.domainmodel;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name="professors")
public class Professor {

	@Id
	@Column(name="username")
	private String username;
	
	@Column(name="professor_name")
	private String professorName;

	@Column(name = "interests")
	private String interests;
	
	@OneToMany(mappedBy="supervisor")
	private List<TraineeshipPosition> supervisedPositions;
	
	
	public Professor(String username, String professorName, String interests,
			List<TraineeshipPosition> supervisedPositions) {
		this.username = username;
		this.professorName = professorName;
		this.interests = interests;
		this.supervisedPositions = supervisedPositions;
	}

	
	public Professor() {

	}


	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getProfessorName() {
		return professorName;
	}
	
	public void setProfessorName(String professorName) {
		this.professorName = professorName;
	}

	public String getInterests() {
		return interests;
	}
	
	public void setInterests(String interests) {
		this.interests = interests;
	}
	
	public List<TraineeshipPosition> getSupervisedPositions() {
		return supervisedPositions;
	}
	
	public void setSupervisedPositions(List<TraineeshipPosition> supervisedPositions) {
		this.supervisedPositions = supervisedPositions;
	}
}
