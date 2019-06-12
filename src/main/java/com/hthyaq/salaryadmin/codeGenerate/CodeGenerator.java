package com.hthyaq.salaryadmin.codeGenerate;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

/**
 * 生成通用代码
 */
public class CodeGenerator {
    public static void main(String[] args) {
        //全局配置
        GlobalConfig gConfig = new GlobalConfig();
        gConfig.setAuthor("zhangqiang")
                .setOutputDir(System.getProperty("user.dir") + "/src/main/java")
                .setOpen(false)
                .setFileOverride(true)
                .setIdType(IdType.AUTO)
                .setServiceName("%sService")
                .setBaseResultMap(true)
                .setBaseColumnList(true);
        //数据源配置
        DataSourceConfig dsConfig = new DataSourceConfig();
        dsConfig.setDbType(DbType.MYSQL)
                .setDriverName("com.mysql.jdbc.Driver")
                .setUrl("jdbc:mysql://localhost:3306/salary?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8&useSSL=true")
                .setUsername("root")
                .setPassword("root");
        //包配置
        PackageConfig pkConfig = new PackageConfig();
        pkConfig.setParent("com.hthyaq.salaryadmin")
                .setController("restController")
                .setEntity("entity")
                .setService("service")
                .setMapper("mapper")
                .setXml("mapper/xml");
        //策略配置
        StrategyConfig stConfig = new StrategyConfig();
        stConfig.setCapitalMode(true)
                .setNaming(NamingStrategy.underline_to_camel)
                .setEntityLombokModel(true)
                .setRestControllerStyle(true)
                //.setTablePrefix("sys_")
                .setInclude("sal_lx");
        //整合
        AutoGenerator ag = new AutoGenerator();
        ag.setGlobalConfig(gConfig)
                .setDataSource(dsConfig)
                .setPackageInfo(pkConfig)
                .setStrategy(stConfig);
        //执行
        ag.setTemplateEngine(new FreemarkerTemplateEngine());
        ag.execute();
    }
}
