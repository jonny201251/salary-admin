package com.hthyaq.salaryadmin.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LabelValueString {
    private String label;
    private String value;
}
