package com.khpi.krykun.vitalii.services;

import com.khpi.krykun.vitalii.model.Employee;
import com.khpi.krykun.vitalii.model.LoadingEntity;
import com.khpi.krykun.vitalii.model.SpecialtyEntity;
import com.khpi.krykun.vitalii.model.Task;
import com.khpi.krykun.vitalii.model.enums.Level;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.khpi.krykun.vitalii.utils.CalendarUtils.addDays;
import static com.khpi.krykun.vitalii.utils.CalendarUtils.isHoliday;

@Service
public class RestrictionsService {

    private static final Double WORK_DAY_HOURS = 8d;
    private static final String DATE_FORMAT = "MM/dd/yyyy";

    public Set<Employee> findPossibleEmployeesForTask(Task task, List<Employee> employees) {
        Set<Employee> possibleEmployees = new LinkedHashSet<>();
        for (Employee employee : employees) {
            if (isCandidateCapable(employee, task)) {
                possibleEmployees.add(employee);
            }
        }
        return possibleEmployees;
    }

    public Set<Task> findPossibleTasksForEmployee(Employee employee, List<Task> tasks) {
        Set<Task> possibleTasks = new LinkedHashSet<>();
        for (Task task : tasks) {
            if (isCandidateCapable(employee, task)) {
                possibleTasks.add(task);
            }
        }
        return possibleTasks;
    }

    public boolean isCandidateCapable(Employee employee, Task task) {
        return isSpecialtySufficient(task, employee) && isSkillsSufficient(task, employee)
                && isTimeSufficient(task, employee);
    }

    private boolean isLevelSufficient(Task task, SpecialtyEntity specialty) {
        return StringUtils.equals(specialty.getSpecialty(), task.getSpecialty()) &&
                Level.find(specialty.getLevel()).getRank() >= Level.find(task.getLevel()).getRank();
    }

    private boolean isSpecialtySufficient(Task task, Employee employee) {
        List<SpecialtyEntity> employeeSpecialties = employee.getSpecialties();
        Optional<SpecialtyEntity> goodSpecialty = employeeSpecialties.stream()
                .filter(specialty -> isLevelSufficient(task, specialty))
                .findAny();
        return goodSpecialty.isPresent();
    }

    private boolean isSkillsSufficient(Task task, Employee employee) {
        return CollectionUtils.containsAll(employee.getSkills(), task.getSkills());
    }

    private boolean isTimeSufficient(Task task, Employee employee) {
        try {
            Date startDate = new SimpleDateFormat(DATE_FORMAT).parse(task.getStartDate());
            Date deadLine = new SimpleDateFormat(DATE_FORMAT).parse(task.getDeadLine());

            Double hoursLeft = Double.parseDouble(task.getComplexity());//check for format hh:mm
            Date currentDate = startDate;
            boolean isTimeSufficient = false;
            do {
                if (!isHoliday(currentDate)) {
                    Double hoursLoaded = 0d;
                    String currentDateString = new SimpleDateFormat(DATE_FORMAT).format(currentDate);
                    LoadingEntity currentDayLoad = employee.getLoadOfWork().stream()
                            .filter(candidateLoad -> StringUtils.equals(candidateLoad.getDate(), currentDateString))
                            .findFirst()
                            .orElse(null);
                    if (currentDayLoad != null) {
                        hoursLoaded = Double.parseDouble(currentDayLoad.getLoad());
                        //or list if it is possible if there is a multiple records for a day
                    }
                    hoursLeft -= (WORK_DAY_HOURS - hoursLoaded);
                    if (hoursLeft <= 0d) {
                        isTimeSufficient = true;
                        break;
                    }
                }
                currentDate = addDays(currentDate, 1);
            } while (currentDate.compareTo(deadLine) <= 0);
            return isTimeSufficient;
        } catch (ParseException e) {
            throw new RuntimeException("Fail to parse date, please use valid format!", e);
        }
    }
}
