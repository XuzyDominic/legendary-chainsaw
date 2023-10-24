package com.xzydominic.xzymq.core.excel.service;

import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import cn.afterturn.easypoi.excel.export.base.ExportCommonService;
import cn.afterturn.easypoi.excel.export.styler.IExcelExportStyler;
import cn.afterturn.easypoi.exception.excel.ExcelExportException;
import cn.afterturn.easypoi.exception.excel.enums.ExcelExportEnum;
import cn.afterturn.easypoi.util.PoiExcelGraphDataUtil;
import cn.afterturn.easypoi.util.PoiMergeCellUtil;
import cn.afterturn.easypoi.util.PoiPublicUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author xuzeyao
 */
@SuppressWarnings("all")
public class ExcelExportService extends BaseExportService {

    private static int MAX_NUM = 60000;

    protected int createHeaderAndTitle(ExportParams entity, Sheet sheet, Workbook workbook,
                                       List<ExcelExportEntity> excelParams) {
        int rows = 0, fieldLength = getFieldLength(excelParams);
        if (entity.getTitle() != null) {
            rows += createTitle2Row(entity, sheet, workbook, fieldLength);
        }
        createHeaderRow(entity, sheet, workbook, rows, excelParams, 0);
        rows += getRowNums(excelParams, true);
        if (entity.isFixedTitle()) {
            sheet.createFreezePane(0, rows, 0, rows);
        }
        return rows;
    }

    private int createHeaderRow(ExportParams title, Sheet sheet, Workbook workbook, int index,
                                List<ExcelExportEntity> excelParams, int cellIndex) {
        Row row = sheet.getRow(index) == null ? sheet.createRow(index) : sheet.getRow(index);
        int rows = getRowNums(excelParams, true);
        row.setHeight(title.getHeaderHeight());
        Row listRow = null;
        if (rows >= 2) {
            listRow = sheet.createRow(index + 1);
            listRow.setHeight(title.getHeaderHeight());
        }
        int groupCellLength = 0;
        CellStyle titleStyle = getExcelExportStyler().getTitleStyle(title.getColor());
        for (int i = 0, exportFieldTitleSize = excelParams.size(); i < exportFieldTitleSize; i++) {
            ExcelExportEntity entity = excelParams.get(i);
            if(i != 0){
                if (StringUtils.isBlank(entity.getGroupName()) || !entity.getGroupName().equals(excelParams.get(i - 1).getGroupName())) {
                    if (groupCellLength > 1) {
                        sheet.addMergedRegion(new CellRangeAddress(index, index, cellIndex - groupCellLength, cellIndex - 1));
                    }
                    groupCellLength = 0;
                }
            }
            if (StringUtils.isNotBlank(entity.getGroupName())) {
                createStringCell(row, cellIndex, entity.getGroupName(), titleStyle, entity);
                createStringCell(listRow, cellIndex, entity.getName(), titleStyle, entity);
                groupCellLength ++;
            } else if (StringUtils.isNotBlank(entity.getName())) {
                createStringCell(row, cellIndex, entity.getName(), titleStyle, entity);
            }
            if (entity.getList() != null) {
                int tempCellIndex = cellIndex;
                cellIndex = createHeaderRow(title, sheet, workbook, rows == 1 ? index : index + 1, entity.getList(), cellIndex);
                List<ExcelExportEntity> sTitel = entity.getList();
                if (StringUtils.isNotBlank(entity.getName()) && sTitel.size() > 1) {
                    PoiMergeCellUtil.addMergedRegion(sheet, index, index, tempCellIndex, tempCellIndex + sTitel.size() - 1);
                }
                cellIndex --;
            } else if (rows > 1 && StringUtils.isBlank(entity.getGroupName())) {
                createStringCell(listRow, cellIndex, "", titleStyle, entity);
                PoiMergeCellUtil.addMergedRegion(sheet, index, index + rows - 1, cellIndex, cellIndex);
            }
            cellIndex ++;
        }
        if (groupCellLength > 1) {
            PoiMergeCellUtil.addMergedRegion(sheet, index, index, cellIndex - groupCellLength, cellIndex - 1);
        }
        return cellIndex;
    }

    public int createTitle2Row(ExportParams entity, Sheet sheet, Workbook workbook, int fieldWidth) {
        Row row = sheet.createRow(0);
        row.setHeight(entity.getTitleHeight());
        createStringCell(row, 0, entity.getTitle(),
                getExcelExportStyler().getHeaderStyle(entity.getHeaderColor()), null);
        for (int i = 1; i <= fieldWidth; i++) {
            createStringCell(row, i, "",
                    getExcelExportStyler().getHeaderStyle(entity.getHeaderColor()), null);
        }
        PoiMergeCellUtil.addMergedRegion(sheet, 0, 0, 0, fieldWidth);
        if (entity.getSecondTitle() != null) {
            row = sheet.createRow(1);
            row.setHeight(entity.getSecondTitleHeight());
            CellStyle style = workbook.createCellStyle();
            style.setAlignment(HorizontalAlignment.RIGHT);
            createStringCell(row, 0, entity.getSecondTitle(), style, null);
            for (int i = 1; i <= fieldWidth; i++) {
                createStringCell(row, i, "",
                        getExcelExportStyler().getHeaderStyle(entity.getHeaderColor()), null);
            }
            PoiMergeCellUtil.addMergedRegion(sheet, 1, 1, 0, fieldWidth);
            return 2;
        }
        return 1;
    }

    public void createSheet(Workbook workbook, ExportParams entity, Class<?> pojoClass,
                            Collection<?> dataSet, Collection<?> alignmentList) {
        if (ExportCommonService.LOGGER.isDebugEnabled()) {
            ExportCommonService.LOGGER.debug("Excel export start, class is {}", pojoClass);
            ExportCommonService.LOGGER.debug("Excel version is {}",
                    entity.getType().equals(ExcelType.HSSF) ? "03" : "07");
        }
        if (workbook == null || entity == null || pojoClass == null || dataSet == null) {
            throw new ExcelExportException(ExcelExportEnum.PARAMETER_ERROR);
        }
        try {
            List<ExcelExportEntity> excelParams = new ArrayList<ExcelExportEntity>();
            Field[] fileds = PoiPublicUtil.getClassFields(pojoClass);
            ExcelTarget etarget = pojoClass.getAnnotation(ExcelTarget.class);
            String targetId = etarget == null ? null : etarget.value();
            getAllExcelField(entity.getExclusions(), targetId, fileds, excelParams, pojoClass,
                    null, null);
            createSheetForMap(workbook, entity, excelParams, dataSet, alignmentList);
        } catch (Exception e) {
            ExportCommonService.LOGGER.error(e.getMessage(), e);
            throw new ExcelExportException(ExcelExportEnum.EXPORT_ERROR, e.getCause());
        }
    }

    public void createSheetForMap(Workbook workbook, ExportParams entity,
                                  List<ExcelExportEntity> entityList, Collection<?> dataSet,
                                  Collection<?> alignmentList) {
        if (ExportCommonService.LOGGER.isDebugEnabled()) {
            ExportCommonService.LOGGER.debug("Excel version is {}",
                    entity.getType().equals(ExcelType.HSSF) ? "03" : "07");
        }
        if (workbook == null || entity == null || entityList == null || dataSet == null) {
            throw new ExcelExportException(ExcelExportEnum.PARAMETER_ERROR);
        }
        super.type = entity.getType();
        if (type.equals(ExcelType.XSSF)) {
            MAX_NUM = 1000000;
        }
        if (entity.getMaxNum() > 0) {
            MAX_NUM = entity.getMaxNum();
        }
        Sheet sheet = null;
        try {
            sheet = workbook.createSheet(entity.getSheetName());
        } catch (Exception e) {
            sheet = workbook.createSheet();
        }
        insertDataToSheet(workbook, entity, entityList, dataSet, sheet, alignmentList);
    }

    protected void insertDataToSheet(Workbook workbook, ExportParams entity,
                                     List<ExcelExportEntity> entityList, Collection<?> dataSet,
                                     Sheet sheet, Collection<?> alignmentList) {
        try {
            dataHandler = entity.getDataHandler();
            if (dataHandler != null && dataHandler.getNeedHandlerFields() != null) {
                needHandlerList = Arrays.asList(dataHandler.getNeedHandlerFields());
            }
            dictHandler = entity.getDictHandler();
            i18nHandler = entity.getI18nHandler();
            setExcelExportStyler((IExcelExportStyler) entity.getStyle()
                    .getConstructor(Workbook.class).newInstance(workbook));
            Drawing patriarch = PoiExcelGraphDataUtil.getDrawingPatriarch(sheet);
            List<ExcelExportEntity> excelParams = new ArrayList<>();
            if (entity.isAddIndex()) {
                excelParams.add(indexExcelEntity(entity));
            }
            excelParams.addAll(entityList);
            sortAllParams(excelParams);
            int index = entity.isCreateHeadRows()
                    ? createHeaderAndTitle(entity, sheet, workbook, excelParams) : 0;
            int titleHeight = index;
            setCellWidth(excelParams, sheet);
            setColumnHidden(excelParams, sheet);
            short rowHeight = entity.getHeight() != 0 ? entity.getHeight() : getRowHeight(excelParams);
            setCurrentIndex(1);
            List<String> alignments = new ArrayList<>();
            Iterator<?> alignment = alignmentList.iterator();
            while (alignment.hasNext()) {
                alignments.add(String.valueOf(alignment.next()));
            }
            Iterator<?> its = dataSet.iterator();
            List<Object> tempList = new ArrayList<>();
            while (its.hasNext()) {
                Object t = its.next();
                index += createCells(patriarch, index, t, excelParams, sheet, workbook, rowHeight, 0, alignments)[0];
                tempList.add(t);
                if (index >= MAX_NUM) {
                    break;
                }
            }
            if (entity.getFreezeCol() != 0) {
                sheet.createFreezePane(entity.getFreezeCol(), 0, entity.getFreezeCol(), 0);
            }
            mergeCells(sheet, excelParams, titleHeight);
            its = dataSet.iterator();
            for (int i = 0, le = tempList.size(); i < le; i ++) {
                its.next();
                its.remove();
            }
            if (ExportCommonService.LOGGER.isDebugEnabled()) {
                ExportCommonService.LOGGER.debug("List data more than max, data size is {}",
                        dataSet.size());
            }
            if (dataSet.size() > 0) {
                createSheetForMap(workbook, entity, entityList, dataSet,alignmentList);
            } else {
                addStatisticsRow(getExcelExportStyler().getStyles(true, null), sheet);
            }

        } catch (Exception e) {
            ExportCommonService.LOGGER.error(e.getMessage(), e);
            throw new ExcelExportException(ExcelExportEnum.EXPORT_ERROR, e.getCause());
        }
    }

}
