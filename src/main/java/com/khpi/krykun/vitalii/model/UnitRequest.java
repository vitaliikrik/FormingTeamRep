package com.khpi.krykun.vitalii.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UnitRequest {
    private List<Task> tasks;
    private List<Employee> employees;
}