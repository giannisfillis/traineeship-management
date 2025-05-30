package myy803.traineeship_management.domainmodel;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name="traineeship_positions")
public class TraineeshipPosition {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="t_id")
	private int id;
	
	@Column(name="title")
	private String title;
	
	@Column(name="descr")
	private String description;
	
	@Column(name="start_date")
	private String startDate;
	
	@Column(name="end_date")
	private String endDate;
	
	@Column(name = "topics")
	private String topics;
	
	@Column(name = "skills")	
	private String requiredSkills;
	
	@Column(name="is_assigned")
	private boolean isAssigned;
	
	@Column(name="logbook")
	private String studentLogBook;
	
	@Column(name="grade")
	private boolean passFailGrade;
	
	@OneToOne(fetch = FetchType.LAZY,
			cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REFRESH}
	)
	@JoinColumn(name = "assigned_student")	
	private Student assignedStudent;
	
	@ManyToOne(fetch = FetchType.LAZY,
			cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REFRESH}
	)
	@JoinColumn(name = "supervisor")		
	private Professor supervisor;
	
	@ManyToOne(fetch = FetchType.LAZY,
			cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REFRESH}
	)
	@JoinColumn(name = "offering_company")		
	private Company offeringCompany;
	
	@OneToMany(mappedBy="evaluatedPosition")
	private List<Evaluation> evaluations;
	
	public TraineeshipPosition(int id, String title, String description, String startDate, String endDate,
			String topics, String requiredSkills, boolean isAssigned, String studentLogBook,
			boolean passFailGrade, Student assignedStudent, Professor supervisor, Company offeringCompany,
			List<Evaluation> evaluations) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.startDate = startDate;
		this.endDate = endDate;
		this.topics = topics;
		this.requiredSkills = requiredSkills;
		this.isAssigned = isAssigned;
		this.studentLogBook = studentLogBook;
		this.passFailGrade = passFailGrade;
		this.assignedStudent = assignedStudent;
		this.supervisor = supervisor;
		this.offeringCompany = offeringCompany;
		this.evaluations = evaluations;
	}

	public TraineeshipPosition() {

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getTopics() {
		return topics;
	}

	public void setTopics(String topics) {
		this.topics = topics;
	}

	public String getRequiredSkills() {
		return requiredSkills;
	}

	public void setRequiredSkills(String requiredSkills) {
		this.requiredSkills = requiredSkills;
	}

	public boolean isAssigned() {
		return isAssigned;
	}

	public void setAssigned(boolean isAssigned) {
		this.isAssigned = isAssigned;
	}

	public String getStudentLogBook() {
		return studentLogBook;
	}

	public void setStudentLogBook(String studentLogBook) {
		this.studentLogBook = studentLogBook;
	}

	public boolean getPassFailGrade() {
		return passFailGrade;
	}

	public void setPassFailGrade(boolean passFailGrade) {
		this.passFailGrade = passFailGrade;
	}

	public Student getAssignedStudent() {
		return assignedStudent;
	}

	public void setAssignedStudent(Student assignedStudent) {
		this.assignedStudent = assignedStudent;
	}

	public Professor getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(Professor supervisor) {
		this.supervisor = supervisor;
	}

	public Company getOfferingCompany() {
		return offeringCompany;
	}

	public void setOfferingCompany(Company offeringCompany) {
		this.offeringCompany = offeringCompany;
	}

	public List<Evaluation> getEvaluations() {
		return evaluations;
	}

	public void setEvaluations(List<Evaluation> evaluations) {
		this.evaluations = evaluations;
	}
	
	public String getCompanyLocation() {
		return this.offeringCompany.getLocation();
	}
	
}
