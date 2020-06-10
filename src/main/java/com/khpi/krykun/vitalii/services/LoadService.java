package com.khpi.krykun.vitalii.services;

import com.khpi.krykun.vitalii.model.Employee;
import com.khpi.krykun.vitalii.model.LoadingEntity;
import com.khpi.krykun.vitalii.model.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.khpi.krykun.vitalii.utils.CalendarUtils.addDays;
import static com.khpi.krykun.vitalii.utils.CalendarUtils.isHoliday;

@Service
public class LoadService {

    private static final Double WORK_DAY_HOURS = 8d;
    private static final String DATE_FORMAT = "MM/dd/yyyy";

    public void loadEmployeeWithTask(Employee employee, Task task) {
        try {
            Date startDate = new SimpleDateFormat(DATE_FORMAT).parse(task.getStartDate());

            Double hoursLeft = Double.parseDouble(task.getComplexity());
            Date currentDate = startDate;
            do {
                if (!isHoliday(currentDate)) {
                    double hoursLoaded;
                    String currentDateString = new SimpleDateFormat(DATE_FORMAT).format(currentDate);
                    LoadingEntity currentDayLoad = employee.getLoadOfWork().stream()
                            .filter(candidateLoad -> StringUtils.equals(candidateLoad.getDate(), currentDateString))
                            .findFirst()
                            .orElse(null);
                    if (currentDayLoad != null) {
                        hoursLoaded = Double.parseDouble(currentDayLoad.getLoad());
                        double freeHours = (WORK_DAY_HOURS - hoursLoaded);
                        //or list if it is possible if there is a multiple records for a day
                        if (hoursLeft > freeHours) {
                            hoursLeft -= freeHours;
                            currentDayLoad.setLoad(String.valueOf(WORK_DAY_HOURS));
                        } else {
                            double hoursToSpend = hoursLeft;
                            hoursLeft-= hoursToSpend;
                            currentDayLoad.setLoad(String.valueOf(hoursLoaded + hoursToSpend));
                        }
                    } else {
                        LoadingEntity loadingForNewDay = new LoadingEntity();
                        if (hoursLeft > WORK_DAY_HOURS) {
                            hoursLeft -= WORK_DAY_HOURS;
                            loadingForNewDay.setLoad(String.valueOf(WORK_DAY_HOURS));
                        } else {
                            double hoursToSpend = hoursLeft;
                            hoursLeft-= hoursToSpend;
                            loadingForNewDay.setLoad(String.valueOf(hoursToSpend));
                        }
                        loadingForNewDay.setDate(currentDateString);
                        employee.getLoadOfWork().add(loadingForNewDay);
                    }
                }
                currentDate = addDays(currentDate, 1);
            } while (hoursLeft > 0d);
        } catch (ParseException e) {
            throw new RuntimeException("Fail to parse date, please use valid format!", e);
        }
    }

}
