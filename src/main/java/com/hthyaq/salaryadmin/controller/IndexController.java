package com.hthyaq.salaryadmin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class IndexController {
    @GetMapping(value = {"/login", "/dept", "/dic", "/user", "/salNp", "/salLtx", "/salLx", "/changeSheet", "/changeSheetNeedHandle", "/permission", "/role", "/main", "/userSalNp"})
    public String index() {
        return "index";
    }


}
