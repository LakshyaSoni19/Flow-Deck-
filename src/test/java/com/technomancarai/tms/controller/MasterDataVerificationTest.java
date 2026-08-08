package com.technomancarai.tms.controller;

import com.technomancarai.tms.TaskManagementSystemApplication;
import com.technomancarai.tms.repository.CityRepository;
import com.technomancarai.tms.repository.CountryRepository;
import com.technomancarai.tms.repository.DepartmentRepository;
import com.technomancarai.tms.repository.DesignationRepository;
import com.technomancarai.tms.repository.RoleRepository;
import com.technomancarai.tms.repository.StateRepository;
import com.technomancarai.tms.repository.TaskPriorityRepository;
import com.technomancarai.tms.repository.TaskStatusRepository;
import com.technomancarai.tms.repository.TaskTypeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = TaskManagementSystemApplication.class)
public class MasterDataVerificationTest {

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DesignationRepository designationRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskPriorityRepository taskPriorityRepository;

    @Autowired
    private TaskTypeRepository taskTypeRepository;

    @Test
    public void verifyMasterTablesSeedData() {
        long countryCount = countryRepository.count();
        long stateCount = stateRepository.count();
        long cityCount = cityRepository.count();
        long departmentCount = departmentRepository.count();
        long designationCount = designationRepository.count();
        long roleCount = roleRepository.count();
        long taskStatusCount = taskStatusRepository.count();
        long taskPriorityCount = taskPriorityRepository.count();
        long taskTypeCount = taskTypeRepository.count();

        System.out.println("=================================================");
        System.out.println("  MASTER TABLE SEED DATA ROW COUNTS REPORT");
        System.out.println("=================================================");
        System.out.println("Country Count: " + countryCount);
        System.out.println("State Count: " + stateCount);
        System.out.println("City Count: " + cityCount);
        System.out.println("Department Count: " + departmentCount);
        System.out.println("Designation Count: " + designationCount);
        System.out.println("Role Count: " + roleCount);
        System.out.println("TaskStatus Count: " + taskStatusCount);
        System.out.println("TaskPriority Count: " + taskPriorityCount);
        System.out.println("TaskType Count: " + taskTypeCount);
        System.out.println("=================================================");

        assertThat(countryCount).isGreaterThan(0);
        assertThat(stateCount).isGreaterThan(0);
        assertThat(cityCount).isGreaterThan(0);
        assertThat(departmentCount).isGreaterThan(0);
        assertThat(designationCount).isGreaterThan(0);
        assertThat(roleCount).isGreaterThan(0);
        assertThat(taskStatusCount).isGreaterThan(0);
        assertThat(taskPriorityCount).isGreaterThan(0);
        assertThat(taskTypeCount).isGreaterThan(0);
    }
}
