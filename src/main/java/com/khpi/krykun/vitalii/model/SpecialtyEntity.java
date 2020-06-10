package com.khpi.krykun.vitalii.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class SpecialtyEntity {
    private String specialty;
    private String level;
}
