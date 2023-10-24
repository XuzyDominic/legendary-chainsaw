package com.xzydominic.xzymq.core.excel.utils;

import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import cn.afterturn.easypoi.excel.export.ExcelBatchExportService;
import cn.afterturn.easypoi.excel.export.template.ExcelExportOfTemplateUtil;
import com.xzydominic.xzymq.core.excel.constant.ExcelConstant;
import com.xzydominic.xzymq.core.excel.service.ExcelExportService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author xuzeyao
 */
@SuppressWarnings("all")
public class ZyExcelExportUtil {

    private ZyExcelExportUtil() {}

    private static final Integer BOUNDARY_VALUE = 100000;

    public static Workbook exportBigExcel(ExportParams entity, Class<?> pojoClass,
                                          Collection<?> dataSet) {
        ExcelBatchExportService batchService = ExcelBatchExportService
                .getExcelBatchExportService(entity, pojoClass);
        return batchService.appendData(dataSet);
    }

    public static Workbook exportBigExcel(ExportParams entity, List<ExcelExportEntity> excelParams,
                                          Collection<?> dataSet) {
        ExcelBatchExportService batchService = ExcelBatchExportService
                .getExcelBatchExportService(entity, excelParams);
        return batchService.appendData(dataSet);
    }

    public static void closeExportBigExcel() {
        ExcelBatchExportService batchService = ExcelBatchExportService.getCurrentExcelBatchExportService();
        if (batchService != null) {
            batchService.closeExportBigExcel();
        }
    }

    public static Workbook exportExcel(ExportParams entity, Class<?> pojoClass,
                                       Collection<?> dataSet, Collection<?> alignmentList) {
        Workbook workbook = getWorkbook(entity.getType(), dataSet.size());
        new ExcelExportService().createSheet(workbook, entity, pojoClass, dataSet, alignmentList);
        return workbook;
    }

    private static Workbook getWorkbook(ExcelType type, int size) {
        if (ExcelType.HSSF.equals(type)) {
            return new HSSFWorkbook();
        } else if (size < BOUNDARY_VALUE) {
            return new XSSFWorkbook();
        } else {
            return new SXSSFWorkbook();
        }
    }

    public static Workbook exportExcel(ExportParams entity, List<ExcelExportEntity> entityList,
                                       Collection<?> dataSet, Collection<?> alignmentList) {
        Workbook workbook = getWorkbook(entity.getType(), dataSet.size());
        new ExcelExportService().createSheetForMap(workbook, entity, entityList, dataSet, alignmentList);
        return workbook;
    }

    public static Workbook exportExcel(List<Map<String, Object>> list, ExcelType type) {
        Workbook workbook = getWorkbook(type, 0);
        for (Map<String, Object> map : list) {
            ExcelExportService service = new ExcelExportService();
            service.createSheet(workbook, (ExportParams) map.get(ExcelConstant.TITLE),
                    (Class<?>) map.get(ExcelConstant.ENTITY), (Collection<?>) map.get(ExcelConstant.DATA),
                    (Collection<?>) map.get(ExcelConstant.ALIGNMENT_LIST));
        }
        return workbook;
    }

    public static Workbook exportMultiSheetExcel(List<Map<String, Object>> list, ExcelType type) {
        Workbook workbook = getWorkbook(type, 0);
        for (Map<String, Object> map : list) {
            new ExcelExportService().createSheetForMap(workbook, (ExportParams) map.get(ExcelConstant.TITLE),
                    (List<ExcelExportEntity>)map.get(ExcelConstant.ENTITY),
                    (Collection<?>) map.get(ExcelConstant.DATA),
                    (Collection<?>) map.get(ExcelConstant.ALIGNMENT_LIST));
        }
        return workbook;
    }

    @Deprecated
    public static Workbook exportExcel(TemplateExportParams params, Class<?> pojoClass,
                                       Collection<?> dataSet, Map<String, Object> map) {
        return new ExcelExportOfTemplateUtil().createExcleByTemplate(params, pojoClass, dataSet,
                map);
    }

    public static Workbook exportExcel(TemplateExportParams params, Map<String, Object> map) {
        return new ExcelExportOfTemplateUtil().createExcleByTemplate(params, null, null, map);
    }

    public static Workbook exportExcel(Map<Integer, Map<String, Object>> map,
                                       TemplateExportParams params) {
        return new ExcelExportOfTemplateUtil().createExcleByTemplate(params, map);
    }

}
