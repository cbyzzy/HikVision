package com.github.cbyzzy.model.dto;

import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;

public class HistoryDTO {
    @ApiModelProperty(name = "开始时间", dataType = "Date")
    private LocalDateTime startTime;

    @ApiModelProperty(name = "结束时间", dataType = "Date")
    private LocalDateTime endTime;

    public HistoryDTO() {}

    public HistoryDTO(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
