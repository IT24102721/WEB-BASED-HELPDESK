package org.example.se2030helpdesk.repository;

import org.example.se2030helpdesk.model.IssueReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface   IssueReportRepository extends JpaRepository<IssueReport, Long> {
    List<IssueReport> findByStatus(IssueReport.Status status);
    List<IssueReport> findByUserIdOrderByReportDateDesc(Long userId);
    List<IssueReport> findByResourceIdOrderByReportDateDesc(Long resourceId);
    List<IssueReport> findByStatusOrderByReportDateAsc(IssueReport.Status status);
}
