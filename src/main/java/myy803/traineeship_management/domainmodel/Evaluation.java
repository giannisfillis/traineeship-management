package myy803.traineeship_management.domainmodel;

import jakarta.persistence.*;

@Entity
@Table(name = "evaluations")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Evaluation {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="e_id")
	private int id;
	
	@ManyToOne(fetch = FetchType.LAZY,
			cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REFRESH}
	)
    @JoinColumn(name = "traineeship_id")
	private TraineeshipPosition evaluatedPosition;
	

	public Evaluation(int id, TraineeshipPosition evaluatedPosition) {
		this.id = id;
		this.evaluatedPosition = evaluatedPosition;
	}
	
	public Evaluation() {
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public TraineeshipPosition getEvaluatedPosition() {
		return evaluatedPosition;
	}

	public void setEvaluatedPosition(TraineeshipPosition evaluatedPosition) {
		this.evaluatedPosition = evaluatedPosition;
	}

}
