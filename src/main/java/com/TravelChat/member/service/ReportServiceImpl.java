package com.TravelChat.member.service;

import com.TravelChat.member.model.Report;
import com.TravelChat.member.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportServiceImpl implements ReportService{
    @Autowired
    private ReportRepository reportRepository;


    @Override
    public void insertReport(Report report) throws Exception {
        reportRepository.insertReport(report);
    }

    @Override
    public int isReportCount(Report report) {
        return reportRepository.isReportCount(report);
    }

    @Override
    public int countAllUnchecked() {
        return reportRepository.countAllUnchecked();
    }

    @Override
    public Report selectByReportNo(int reportNo) {
        return reportRepository.selectByReportNo(reportNo);
    }
}
