package myy803.traineeship_management.domainmodel;

import jakarta.persistence.*;

@Entity
public class CompanyEvaluation extends Evaluation {
	
	@Column(name="facilities")
	private int facilitiesGrade;
	
	@Column(name="guidance")
	private int guidanceGrade;
	
	
	public CompanyEvaluation(int id, TraineeshipPosition position, int facilitiesGrade, int guidanceGrade) {
		super(id,position);
		this.facilitiesGrade = facilitiesGrade;
		this.guidanceGrade = guidanceGrade;
	}
	
	public CompanyEvaluation() {
		
	}

	public int getFacilitiesGrade() {
		return facilitiesGrade;
	}


	public void setFacilitiesGrade(int facilitiesGrade) {
		this.facilitiesGrade = facilitiesGrade;
	}


	public int getGuidanceGrade() {
		return guidanceGrade;
	}


	public void setGuidanceGrade(int guidanceGrade) {
		this.guidanceGrade = guidanceGrade;
	}

}
