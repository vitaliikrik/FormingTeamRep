package com.khpi.krykun.vitalii.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Employee {
    @EqualsAndHashCode.Include
    private String uniqueWorkerId;
    private String firstName;
    private String lastName;
    private String project;
    private List<SpecialtyEntity> specialties;
    private List<String> skills;
    private List<LoadingEntity> loadOfWork;
}
