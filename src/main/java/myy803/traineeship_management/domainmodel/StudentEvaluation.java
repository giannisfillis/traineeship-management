package myy803.traineeship_management.domainmodel;

import jakarta.persistence.*;

@Entity
public class StudentEvaluation extends Evaluation {
	
	@Column(name="motivation")
	private int motivationGrade;
	
	@Column(name="efficiency")
	private int efficiencyGrade;
	
	@Column(name="effectiveness")
	private int effectivenessGrade;
	
	@Column(name="by_company")
	private boolean byCompany;
	

	public StudentEvaluation(int id, TraineeshipPosition position, int motivationGrade, int efficiencyGrade, int effectivenessGrade) {
		super(id,position);
		this.motivationGrade = motivationGrade;
		this.efficiencyGrade = efficiencyGrade;
		this.effectivenessGrade = effectivenessGrade;
	}
	
	public StudentEvaluation() {
		
	}
	
	public int getMotivationGrade() {
		return motivationGrade;
	}

	public void setMotivationGrade(int motivationGrade) {
		this.motivationGrade = motivationGrade;
	}

	public int getEfficiencyGrade() {
		return efficiencyGrade;
	}

	public void setEfficiencyGrade(int efficiencyGrade) {
		this.efficiencyGrade = efficiencyGrade;
	}

	public int getEffectivenessGrade() {
		return effectivenessGrade;
	}

	public void setEffectivenessGrade(int effectivenessGrade) {
		this.effectivenessGrade = effectivenessGrade;
	}
	
	public boolean isByCompany() {
		return byCompany;
	}

	public void setByCompany(boolean byCompany) {
		this.byCompany = byCompany;
	}
}

