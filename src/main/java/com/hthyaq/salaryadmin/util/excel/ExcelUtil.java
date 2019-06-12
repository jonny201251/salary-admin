package com.hthyaq.salaryadmin.util.excel;

import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.google.common.base.Throwables;
import com.hthyaq.salaryadmin.util.CloseStreamUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

@Slf4j
public class ExcelUtil {
    /*
        生成excel,只有一个sheet
        file=路径+文件名
     */
    public static void generateExcel(String file, List<? extends BaseRowModel> dataList, Class objClass) {
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            ExcelWriter writer = new ExcelWriter(outputStream, getExcelTypeEnum(file));
            writer.write(dataList, new Sheet(1, 2, objClass)).finish();
        } catch (Exception e) {
            log.error("生成Excel失败了！");
            log.error(Throwables.getStackTraceAsString(e));
        } finally {
            CloseStreamUtil.close(outputStream);
        }
    }

    public static void generateExcelMoreSheet(String file, Map<String, List<? extends BaseRowModel>> map, Class objClass) {
        generateExcelMoreSheet(file, map, objClass, true);
    }

    /*
    生成excel,多个sheet
    file=路径+文件名
    map
        key-sheet的名字
        value-数据
    */
    public static void generateExcelMoreSheet(String file, Map<String, List<? extends BaseRowModel>> map, Class objClass, boolean needHead) {
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            ExcelWriter writer = new ExcelWriter(outputStream, getExcelTypeEnum(file), needHead);
            int sheetNo = 1;
            for (String sheetName : map.keySet()) {
                writer.write(map.get(sheetName), new Sheet(sheetNo, 0, objClass, sheetName, null));
                sheetNo++;
            }
/*            写第一个sheet
            writer.write(list.get(0), new Sheet(1, 0, objClass,"站发",null));
            写第二个sheet
            writer.write(list.get(1), new Sheet(2, 3, objClass,"院发",null))*/
            writer.finish();
        } catch (Exception e) {
            log.error("生成Excel失败了！");
            log.error(Throwables.getStackTraceAsString(e));
        }
    }

    public static void readExcel(MultipartFile multipartFile, Class<? extends BaseRowModel> objClass, List<Object> datas) {
        InputStream inputStream = null;
        try {
            inputStream = multipartFile.getInputStream();
            String fileName = multipartFile.getOriginalFilename();
            readExcel(inputStream, fileName, objClass, datas);
        } catch (Exception e) {
            log.error("读取Excel失败了！");
            log.error(Throwables.getStackTraceAsString(e));
        } finally {
            CloseStreamUtil.close(inputStream);
        }
    }

    public static void readExcel(InputStream inputStream, String fileName, Class<? extends BaseRowModel> objClass, List<Object> datas) {
        // 解析每行结果在listener中处理
        ExcelReader excelReader = new ExcelReader(inputStream, getExcelTypeEnum(fileName), null, new ExcelListener(datas));
        //headLineMun 从第二行开始读数据
        excelReader.read(new Sheet(1, 1, objClass));
        CloseStreamUtil.close(inputStream);
    }

    //读取excel，包括表头
    public static void readExcelIncludeHeader(MultipartFile multipartFile, Class<? extends BaseRowModel> objClass, List<Object> datas) {
        InputStream inputStream = null;
        try {
            inputStream = multipartFile.getInputStream();
            String fileName = multipartFile.getOriginalFilename();
            readExcelIncludeHeader(inputStream, fileName, objClass, datas);
        } catch (Exception e) {
            log.error("读取Excel失败了！");
            log.error(Throwables.getStackTraceAsString(e));
        } finally {
            CloseStreamUtil.close(inputStream);
        }
    }

    //读取excel，包括表头
    public static void readExcelIncludeHeader(InputStream inputStream, String fileName, Class<? extends BaseRowModel> objClass, List<Object> datas) {
        // 解析每行结果在listener中处理
        ExcelReader excelReader = new ExcelReader(inputStream, getExcelTypeEnum(fileName), null, new ExcelListener(datas));
        //headLineMun 从第一行开始读数据
        excelReader.read(new Sheet(1, 0, objClass));
        CloseStreamUtil.close(inputStream);
    }

    //根据文件获取excel的后缀
    private static ExcelTypeEnum getExcelTypeEnum(String filePath) {
        String suffix = filePath.substring(filePath.lastIndexOf(".") + 1);
        if ("xlsx".equals(suffix)) {
            return ExcelTypeEnum.XLSX;
        }
        return ExcelTypeEnum.XLS;
    }


    // 模型 解析监听器
    @Data
    private static class ExcelListener extends AnalysisEventListener {
        private List<Object> datas;

        ExcelListener(List<Object> datas) {
            this.datas = datas;
        }

        @Override
        public void invoke(Object object, AnalysisContext context) {
            datas.add(object);
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
        }

    }
}
