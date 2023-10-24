package com.xzydominic.xzymq.core.excel;

import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import org.apache.poi.ss.usermodel.*;

import java.util.List;
import java.util.Map;

/**
 * @author xuzeyao
 */
@SuppressWarnings("all")
public interface ExportService {

    int[] createCells(Drawing patriarch, int index, Object t,
                      List<ExcelExportEntity> excelParams, Sheet sheet, Workbook workbook,
                      short rowHeight, int cellNum, List<String> alignmentList);

    void createImageCell(Drawing patriarch, ExcelExportEntity entity, Row row, int i,
                         String imagePath, Object obj) throws Exception;

    void createImageCell(Cell cell, double height, String imagePath, byte[] data);

    int createIndexCell(Row row, int index, ExcelExportEntity excelExportEntity);

    void createListCells(Drawing patriarch, int index, int cellNum, Object obj,
                         List<ExcelExportEntity> excelParams, Sheet sheet,
                         Workbook workbook, short rowHeight) throws Exception;

    void createStringCell(Row row, int index, String text, CellStyle style,
                          ExcelExportEntity entity);

    void createDoubleCell(Row row, int index, String text, CellStyle style,
                          ExcelExportEntity entity);

    void addStatisticsRow(CellStyle styles, Sheet sheet);

    void addStatisticsData(Integer index, String text, ExcelExportEntity entity);

    int getImageType(byte[] value);

    Map<Integer, int[]> getMergeDataMap(List<ExcelExportEntity> excelParams);

    CellStyle getStyles(boolean needOne, ExcelExportEntity entity);

    void mergeCells(Sheet sheet, List<ExcelExportEntity> excelParams, int titleHeight);

    void setCellWidth(List<ExcelExportEntity> excelParams, Sheet sheet);

    void setColumnHidden(List<ExcelExportEntity> excelParams, Sheet sheet);

}
