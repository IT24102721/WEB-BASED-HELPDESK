package org.example.se2030helpdesk.service;

import org.example.se2030helpdesk.model.IssueReport;
import org.example.se2030helpdesk.model.Resource;
import org.example.se2030helpdesk.repository.IssueReportRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class IssueReportService {
    private final IssueReportRepository issueReportRepository;
    private final ResourceService resourceService;

    public IssueReportService(IssueReportRepository issueReportRepository, ResourceService resourceService) {
        this.issueReportRepository = issueReportRepository;
        this.resourceService = resourceService;
    }

    public List<IssueReport> findAll() {
        return issueReportRepository.findAll();
    }

    public Optional<IssueReport> findById(Long id) {
        return issueReportRepository.findById(id);
    }

    public List<IssueReport> findByStatus(IssueReport.Status status) {
        return issueReportRepository.findByStatusOrderByReportDateAsc(status);
    }

    public List<IssueReport> findByUserId(Long userId) {
        return issueReportRepository.findByUserIdOrderByReportDateDesc(userId);
    }

    public List<IssueReport> findByResourceId(Long resourceId) {
        return issueReportRepository.findByResourceIdOrderByReportDateDesc(resourceId);
    }

    public IssueReport submitReport(IssueReport report) {
        report.setStatus(IssueReport.Status.OPEN);
        
        // Optionally set resource to under maintenance if it's a critical issue
        // This could be enhanced with severity levels
        
        return issueReportRepository.save(report);
    }

    public IssueReport updateStatus(Long reportId, IssueReport.Status status, String adminComments) {
        IssueReport report = issueReportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Issue report not found"));
        
        report.setStatus(status);
        report.setAdminComments(adminComments);
        
        if (status == IssueReport.Status.RESOLVED) {
            report.setResolvedDate(Instant.now());
            // Set resource back to available if it was under maintenance
            Resource resource = report.getResource();
            if (resource.getStatus() == Resource.Status.UNDER_MAINTENANCE) {
                resourceService.updateStatus(resource.getId(), Resource.Status.AVAILABLE);
            }
        } else if (status == IssueReport.Status.IN_PROGRESS) {
            // Set resource to under maintenance
            resourceService.updateStatus(report.getResource().getId(), Resource.Status.UNDER_MAINTENANCE);
        }
        
        return issueReportRepository.save(report);
    }

    public void deleteById(Long id) {
        issueReportRepository.deleteById(id);
    }
}
