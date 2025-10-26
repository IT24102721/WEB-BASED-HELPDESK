package org.example.se2030helpdesk.controller;

import org.example.se2030helpdesk.model.IssueReport;
import org.example.se2030helpdesk.service.IssueReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/issue-reports")
@CrossOrigin
public class IssueReportController {
    private final IssueReportService issueReportService;

    public IssueReportController(IssueReportService issueReportService) {
        this.issueReportService = issueReportService;
    }

    @GetMapping
    public List<IssueReport> getAllReports(@RequestParam(required = false) String status) {
        if (status != null) {
            return issueReportService.findByStatus(IssueReport.Status.valueOf(status));
        }
        return issueReportService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<IssueReport> getReportById(@PathVariable Long id) {
        return issueReportService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public List<IssueReport> getReportsByUser(@PathVariable Long userId) {
        return issueReportService.findByUserId(userId);
    }

    @GetMapping("/resource/{resourceId}")
    public List<IssueReport> getReportsByResource(@PathVariable Long resourceId) {
        return issueReportService.findByResourceId(resourceId);
    }

    @PostMapping
    public ResponseEntity<IssueReport> submitReport(@RequestBody IssueReport report) {
        try {
            IssueReport savedReport = issueReportService.submitReport(report);
            return ResponseEntity.ok(savedReport);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<IssueReport> updateReportStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> statusUpdate) {
        try {
            IssueReport.Status status = IssueReport.Status.valueOf(statusUpdate.get("status"));
            String comments = statusUpdate.get("comments");
            IssueReport updatedReport = issueReportService.updateStatus(id, status, comments);
            return ResponseEntity.ok(updatedReport);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReport(@PathVariable Long id) {
        try {
            issueReportService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
