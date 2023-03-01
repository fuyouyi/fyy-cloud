package com.fyy.common.tools.utils;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.merge.AbstractMergeStrategy;
import com.alibaba.fastjson2.JSON;
import com.fyy.common.tools.exception.RenException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.*;

/**
 * Excel工具类
 *
 * @author carl
 * @since 1.0.0
 */
@Slf4j
public class ExcelUtils {

    /**
     * 【推荐使用】导入Excel
     */
    public static <T> List<T> aliImportExcel(MultipartFile file, Class<T> pojoClass) {
        if (file == null) {
            return null;
        }
        try {
            return EasyExcel.read(file.getInputStream(), pojoClass, new SyncLimitReadListener()).sheet(0).doReadSync();
        } catch (IOException ioException) {
            throw new RenException(ioException.getMessage());
        }
    }

    /**
     * 【推荐使用】导入Excel，指定第几行开始读
     */
    public static <T> List<T> aliImportExcel(MultipartFile file, Class<T> pojoClass, Integer headRowNumber) {
        if (file == null) {
            return null;
        }
        try {
            return EasyExcel.read(file.getInputStream(), pojoClass, new SyncLimitReadListener()).sheet(0).headRowNumber(headRowNumber).doReadSync();
        } catch (IOException ioException) {
            throw new RenException(ioException.getMessage());
        }
    }


    /**
     * 导入Excel，并从第二行开始校验表头
     */
    public static <T> List<T> aliImportExcelValidateHead(MultipartFile file, Class<T> pojoClass) {
        if (file == null) {
            return null;
        }
        try {
            return EasyExcel.read(file.getInputStream(), pojoClass, new SyncLimitReadListener(pojoClass)).sheet(0).doReadSync();
        } catch (IOException ioException) {
            throw new RenException(ioException.getMessage());
        }
    }

    /**
     * 导入Excel，并从第二行开始校验表头
     */
    public static <T> List<T> aliImportExcelValidateHead(MultipartFile file, Class<T> pojoClass, Integer headRowNumber) {
        if (file == null) {
            return null;
        }
        try {
            return EasyExcel.read(file.getInputStream(), pojoClass, new SyncLimitReadListener(pojoClass)).sheet(0).headRowNumber(headRowNumber).doReadSync();
        } catch (IOException ioException) {
            throw new RenException(ioException.getMessage());
        }
    }

    /**
     * 【推荐使用】导出Excel
     */
    public static <T> void aliExportExcel(HttpServletResponse response, String fileName, List<?> sourceList, Class<T> targetClass) {
        aliExportExcel(response, fileName, sourceList, targetClass, null);
    }

    /**
     * 【推荐使用】导出Excel, 并过滤字段
     */
    public static <T> void aliExportExcel(HttpServletResponse response, String fileName, List<?> sourceList, Class<T> targetClass, Set<String> excludeParams) {
        aliExportExcel(response, fileName, sourceList, targetClass, excludeParams, null);
    }

    /**
     * 【推荐使用】导出Excel, 并过滤字段, 且合并单元格
     */
    public static <T> void aliExportExcel(HttpServletResponse response, String fileName, List<?> sourceList, Class<T> targetClass, Set<String> excludeParams, AbstractMergeStrategy abstractMergeStrategy) {
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyExcel没有关系
        try {
            String fullFileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fullFileName + ".xlsx");
            writeExcelOutputStream(response.getOutputStream(), sourceList, targetClass, excludeParams, abstractMergeStrategy);
        } catch (IOException ioException) {
            throw new RenException(ioException.getMessage());
        }
    }

    /**
     * 【推荐使用】生成MultipartFile
     */
    public static <T> MultipartFile getMultipartFile(String fileName, List<?> targetList, Class<T> targetClass) {
        FileItem item = new DiskFileItemFactory().createItem("file", "text/plain", true, fileName);
        try (OutputStream outputStream = item.getOutputStream()) {
            writeExcelOutputStream(outputStream, targetList, targetClass);
        } catch (Exception e) {
            log.error("生成MultipartFile文件流失败", e);
        }
        return new CommonsMultipartFile(item);
    }

    public static <T> void writeExcelOutputStream(OutputStream outputStream, List<?> targetList, Class<T> targetClass) {
        writeExcelOutputStream(outputStream, targetList, targetClass, null);
    }

    public static <T> void writeExcelOutputStream(OutputStream outputStream, List<?> sourceList, Class<T> targetClass, Set<String> excludeParams) {
        writeExcelOutputStream(outputStream, sourceList, targetClass, excludeParams, null);
    }

    public static <T> void writeExcelOutputStream(OutputStream outputStream, List<?> sourceList, Class<T> targetClass, Set<String> excludeParams, AbstractMergeStrategy abstractMergeStrategy) {
        ExcelWriterBuilder excelWriterBuilder = EasyExcel.write(outputStream, targetClass);
        if (CollectionUtil.isNotEmpty(excludeParams)) {
            excelWriterBuilder.excludeColumnFiledNames(excludeParams);
        }
        if (abstractMergeStrategy != null) {
            excelWriterBuilder.registerWriteHandler(abstractMergeStrategy);
        }
        if (CollectionUtil.isNotEmpty(sourceList) && sourceList.get(0).getClass().equals(targetClass)) {
            excelWriterBuilder.sheet("sheet1").doWrite(sourceList);
        } else {
            List<T> targetList = ConvertUtils.sourceToTarget(sourceList, targetClass);
            excelWriterBuilder.sheet("sheet1").doWrite(targetList);
        }
    }

    public static <T> void aliExportExcelTable(HttpServletResponse response, String fileName, List<?> sourceList, Class<T> targetClass){
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        try {
            String fullFileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fullFileName + ".xlsx");
            List<?> targetList = null;
            if( CollectionUtil.isNotEmpty(sourceList) && sourceList.get(0).getClass().equals(targetClass)){
                targetList = sourceList;
            }
            else {
                targetList = ConvertUtils.sourceToTarget(sourceList, targetClass);
            }
            EasyExcel.write(response.getOutputStream())
                    .excelType(ExcelTypeEnum.XLS)
                    .head(new ArrayList<List<String>>())
                    .sheet("sheet1")
                    .doWrite(targetList);
        } catch (Exception e) {
            // 重置response
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            Map<String, String> map = new HashMap<String, String>();
            map.put("status", "failure");
            map.put("message", "下载文件失败" + e.getMessage());
            try {
                response.getWriter().println(JSON.toJSONString(map));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
