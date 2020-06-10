package com.khpi.krykun.vitalii.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class LoadingEntity {
    private String date;
    private String load;
}
