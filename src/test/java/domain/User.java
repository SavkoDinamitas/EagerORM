package domain;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class User {
    private Integer id;
    private String name;
    private String email;
    private int age;
}
