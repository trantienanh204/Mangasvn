package com.example.demo.baove.controller.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsDTO {
    private long totalComics;
    private long totalChapters;
    private long totalUsers;
    private long totalComments;
    private long totalViews;
    private long totalReports;
}
