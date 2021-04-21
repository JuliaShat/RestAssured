package entities;

import lombok.*;

import java.math.BigInteger;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Category {
    private int id;
    private String name;
}
