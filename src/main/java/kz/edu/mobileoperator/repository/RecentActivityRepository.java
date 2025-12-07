package kz.edu.mobileoperator.repository;

import java.util.List;
import kz.edu.mobileoperator.model.RecentActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с журналом недавней активности.
 */
@Repository
public interface RecentActivityRepository extends JpaRepository<RecentActivity, Long> {

    List<RecentActivity> findTop10ByOrderByCreatedAtDesc();
}


