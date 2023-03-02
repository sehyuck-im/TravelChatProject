package com.TravelChat.member.repository;

import com.TravelChat.member.model.Report;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ReportRepository {

    void insertReport(Report report) throws Exception;

    int isReportCount(Report report);

    int countAllUnchecked();

    Report selectByReportNo(int reportNo);
}
