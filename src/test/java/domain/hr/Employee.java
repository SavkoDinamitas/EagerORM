package domain.hr;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor @NoArgsConstructor
public class Employee {
    private int employee_id;
    private String first_name;
    private String last_name;
    private LocalDate hire_date;
    private Department department;
}
