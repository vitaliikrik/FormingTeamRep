package com.khpi.krykun.vitalii.restful;

import com.khpi.krykun.vitalii.model.UnitRequest;
import com.khpi.krykun.vitalii.services.AlgorithmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/restful")
public class UnitRestfulApi extends SpringBeanAutowiringSupport {

    private static final Logger log = LoggerFactory.getLogger(UnitRestfulApi.class);

    @Autowired
    private AlgorithmService algorithmService;

    @PostMapping("/findTasksForWorkers")
    public Map<String, String> findTasksForWorkers(@RequestBody UnitRequest unitRequest) {
        try {
            Map<String, String> tasksToUsers = algorithmService.findByDispatcherAlgorithm(unitRequest);
            return tasksToUsers;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new HashMap<>();
        }
    }

}
