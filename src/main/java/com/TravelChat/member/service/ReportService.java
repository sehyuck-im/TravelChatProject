package com.TravelChat.member.service;

import com.TravelChat.member.model.Report;

public interface ReportService {
    void insertReport(Report report) throws Exception;

    int isReportCount(Report report);

    int countAllUnchecked();

    Report selectByReportNo(int reportNo);
}
