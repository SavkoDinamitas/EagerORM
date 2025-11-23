package domain.hr;

import lombok.*;

import java.util.List;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class Department {
    private int department_id;
    private String department_name;
    private List<Employee> employees;
}
