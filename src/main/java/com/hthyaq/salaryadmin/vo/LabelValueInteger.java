package com.hthyaq.salaryadmin.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
public class LabelValueInteger {
    private String label;
    private Integer value;
}
