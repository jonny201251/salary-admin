package com.hthyaq.salaryadmin.vo;

import lombok.Data;

import java.util.HashMap;
import java.util.List;

@Data
public class LoginPage {
    private Integer userId;
    private String userName;
    private String salNpYearMonth;
    private String salLtxYearMonth;
    private String salLxYearMonth;
    HashMap<String, List<String>> buttons=new HashMap<>();
}
