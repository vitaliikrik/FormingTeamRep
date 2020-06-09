package com.khpi.krykun.vitalii.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UnitRequest {
    private List<Task> tasks;
    private List<Employee> employees;
}