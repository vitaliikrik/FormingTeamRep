package com.khpi.krykun.vitalii.services;

import com.khpi.krykun.vitalii.model.Employee;
import com.khpi.krykun.vitalii.model.Task;
import com.khpi.krykun.vitalii.model.UnitRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class AlgorithmService {

    @Autowired
    private RestrictionsService restrictionsService;

    @Autowired
    private SolutionService solutionService;

    public Map<String, String> findByDispatcherAlgorithm(UnitRequest unitRequest) {
        //generalized obtaining of employees from HRM-system API
        List<Employee> employees = getEmployeesFromUnitRequest(unitRequest);
        //generalized obtaining of tasks from TMS API
        List<Task> tasks = getTasksFromUnitRequest(unitRequest);

        //get X and Y mappings
        Map<Employee, Set<Task>> employeesMap = getEmployeesMap(employees, tasks);
        Map<Task, Set<Employee>> tasksMap = getTasksMap(tasks, employees);

        //work algorithm
        Map<Task, Employee> finalAssignment = solutionService.getAssignmentSolution(tasksMap, employeesMap);

        return extractIds(finalAssignment);
    }

    private List<Employee> getEmployeesFromUnitRequest(UnitRequest unitRequest) {
        return unitRequest.getEmployees();
    }

    private List<Task> getTasksFromUnitRequest(UnitRequest unitRequest) {
        return unitRequest.getTasks();
    }

    private Map<Employee, Set<Task>> getEmployeesMap(List<Employee> employees, List<Task> tasks) {
        Map<Employee, Set<Task>> employeesMap = new LinkedHashMap<>();
        for (Employee employee : employees) {
            Set<Task> possibleTasks = restrictionsService.findPossibleTasksForEmployee(employee, tasks);
            if (possibleTasks.size() > 0) {
                employeesMap.put(employee, possibleTasks);
            }
        }
        return employeesMap;
    }

    private Map<Task, Set<Employee>> getTasksMap(List<Task> tasks, List<Employee> employees) {
        Map<Task, Set<Employee>> tasksMap = new LinkedHashMap<>();
        for (Task task : tasks) {
            Set<Employee> possibleEmployees = restrictionsService.findPossibleEmployeesForTask(task, employees);
            if (possibleEmployees.size() > 0) {
                tasksMap.put(task, possibleEmployees);
            }
        }
        return tasksMap;
    }

    private Map<String, String> extractIds(Map<Task, Employee> taskToWorkersMap) {
        Map<String, String> result = new LinkedHashMap<>();
        taskToWorkersMap.forEach((key, value) -> result.put(key.getUniqueTaskId(),
                value.getUniqueWorkerId()));
        return result;
    }
}
