package com.khpi.krykun.vitalii.services;

import com.google.gson.Gson;
import com.khpi.krykun.vitalii.model.Employee;
import com.khpi.krykun.vitalii.model.Task;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SolutionService {

    private static final Gson gson;

    static {
        gson = new Gson();
    }

    @Autowired
    private RestrictionsService restrictionsService;

    @Autowired
    private LoadService loadService;

    public Map<Task, Employee> getAssignmentSolution(Map<Task, Set<Employee>> tasksMap,
                                                     Map<Employee, Set<Task>> employeesMap) {
        Map<Task, Employee> resultMap = new LinkedHashMap<>();
        while (CollectionUtils.size(tasksMap.keySet()) > 0) {
            //sorted by first optimization condition, can be replaced to use another one
            Task currentTask = getNextTaskToBeProcessed(tasksMap);

            Map<Employee, Set<Task>> alongsideMap = getAlongsideMap(currentTask, employeesMap, tasksMap.get(currentTask));

            Employee employeeToWork = findBestEmployee(employeesMap, alongsideMap);

            //assigning employee to task
            loadService.loadEmployeeWithTask(employeeToWork, currentTask);
            resultMap.put(currentTask, employeeToWork);

            Set<Task> tasksUnavailableNow = new HashSet<>(SetUtils.difference(employeesMap.get(employeeToWork),
                    alongsideMap.get(employeeToWork)));
            tasksUnavailableNow.remove(currentTask);

            //refreshing of employeesMap
            if (CollectionUtils.size(alongsideMap.get(employeeToWork)) > 0) {//set utils size
                employeesMap.put(employeeToWork, alongsideMap.get(employeeToWork));
            } else {
                employeesMap.remove(employeeToWork);
            }
            tasksMap.get(currentTask)
                    .forEach(employee -> Optional.ofNullable(employeesMap.get(employee))
                            .ifPresent(set -> set.remove(currentTask)));
            //add delete logic here, if after removing there is no tasks for employee to work on

            //refreshing of tasksMap
            tasksMap.remove(currentTask);
            tasksUnavailableNow
                    .forEach(taskToDeleteEmployee -> tasksMap.get(taskToDeleteEmployee).remove(employeeToWork));
            //add delete logic here, if after removing there is no employee to work on task
        }
        return resultMap;
    }

    private Task getNextTaskToBeProcessed(Map<Task, Set<Employee>> tasksMap) {
        Task taskWithMinimalSet = null;
        int minimalSize = Integer.MAX_VALUE;
        for (Map.Entry<Task, Set<Employee>> entry : tasksMap.entrySet()) {
            int numberOfCandidates = CollectionUtils.size(entry.getValue());
            if (numberOfCandidates < minimalSize) {
                taskWithMinimalSet = entry.getKey();
                minimalSize = numberOfCandidates;
            }
        }
        return taskWithMinimalSet;
    }

    private Map<Employee, Set<Task>> getAlongsideMap(Task task, Map<Employee, Set<Task>> employeesMap, Set<Employee> capableEmployees) {
        Map<Employee, Set<Task>> resultAlongsideMap = new LinkedHashMap<>();
        for (Map.Entry<Employee, Set<Task>> entry : employeesMap.entrySet()) {
            Employee currentEmployee = entry.getKey();
            if (capableEmployees.contains(currentEmployee)) {//do not include those who cannot complete task
                Employee currentEmployeeLoaded = getDeepCopy(currentEmployee);//loaded copy
                loadService.loadEmployeeWithTask(currentEmployeeLoaded, task);
                resultAlongsideMap.put(currentEmployee, new LinkedHashSet<>());
                for (Task taskCandidate : entry.getValue()) {
                    if (!StringUtils.equals(taskCandidate.getUniqueTaskId(), task.getUniqueTaskId())) {
                        if (restrictionsService.isCandidateCapable(currentEmployeeLoaded, taskCandidate)) { //loaded copy
                            resultAlongsideMap.get(currentEmployee).add(taskCandidate);
                        }
                    }
                }
            }
        }
        return resultAlongsideMap;
    }

    private Employee findBestEmployee(Map<Employee, Set<Task>> employeesMap, Map<Employee, Set<Task>> alongsideMap) {
        Employee bestEmployee = null;
        int minDifference = Integer.MAX_VALUE;
        for (Map.Entry<Employee, Set<Task>> entry : alongsideMap.entrySet()) {
            Employee currentEmployee = entry.getKey();
            int currentDifference = CollectionUtils.size(employeesMap.get(currentEmployee)) - CollectionUtils.size(entry.getValue());
            if (currentDifference < minDifference) {
                minDifference = currentDifference;
                bestEmployee = currentEmployee;
            }
        }
        return bestEmployee;
    }

    private Employee getDeepCopy(Employee employee) {
        return gson.fromJson(gson.toJson(employee), Employee.class);
    }
}
