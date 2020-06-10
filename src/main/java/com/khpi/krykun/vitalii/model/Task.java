package com.khpi.krykun.vitalii.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Task {
    @EqualsAndHashCode.Include
    private String uniqueTaskId;
    private String summary;
    private String description;
    private String complexity;
    private String startDate;
    private String deadLine;
    private String specialty;
    private String level;
    private List<String> skills;
}
