package myy803.traineeship_management.domainmodel;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name="companies")
public class Company {

	@Id
	@Column(name="username")
	private String username;
	
	@Column(name="company_name")
	private String companyName;
	
	@Column(name="location")
	private String location;
	
	@OneToMany(mappedBy="offeringCompany")
	private List<TraineeshipPosition> positions;
	
	
	public Company(String username, String companyName, String location, List<TraineeshipPosition> positions) {
		this.username = username;
		this.companyName = companyName;
		this.location = location;
		this.positions = positions;
	}
	
	public Company() {
		
	}

	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getCompanyName() {
		return companyName;
	}
	
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public List<TraineeshipPosition> getPositions() {
		return positions;
	}
	
	public void setPositions(List<TraineeshipPosition> positions) {
		this.positions = positions;
	}
	
	public String toString() {
		return this.companyName;
	}
}
