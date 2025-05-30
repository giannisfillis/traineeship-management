package myy803.traineeship_management.mappers;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import myy803.traineeship_management.domainmodel.CompanyEvaluation;
import myy803.traineeship_management.domainmodel.Evaluation;
import myy803.traineeship_management.domainmodel.StudentEvaluation;
import myy803.traineeship_management.domainmodel.TraineeshipPosition;

@Repository
public interface EvaluationMapper extends JpaRepository<Evaluation,Integer>{

	Evaluation findById(int id);
	List<Evaluation> findByEvaluatedPosition(TraineeshipPosition position);
	Optional<CompanyEvaluation> findCompanyEvaluationByEvaluatedPosition(TraineeshipPosition position);
	Optional<StudentEvaluation> findByEvaluatedPositionAndByCompany(TraineeshipPosition position, boolean byCompany);
}
