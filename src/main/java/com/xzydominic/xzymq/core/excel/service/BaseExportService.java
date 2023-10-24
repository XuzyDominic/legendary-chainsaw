package com.xzydominic.xzymq.core.excel.service;

import cn.afterturn.easypoi.cache.ImageCache;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import cn.afterturn.easypoi.excel.entity.vo.BaseEntityTypeConstants;
import cn.afterturn.easypoi.excel.entity.vo.PoiBaseConstants;
import cn.afterturn.easypoi.excel.export.base.ExportCommonService;
import cn.afterturn.easypoi.excel.export.styler.IExcelExportStyler;
import cn.afterturn.easypoi.exception.excel.ExcelExportException;
import cn.afterturn.easypoi.exception.excel.enums.ExcelExportEnum;
import cn.afterturn.easypoi.util.PoiExcelGraphDataUtil;
import cn.afterturn.easypoi.util.PoiMergeCellUtil;
import cn.afterturn.easypoi.util.PoiPublicUtil;
import com.xzydominic.xzymq.core.excel.ExportService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

import java.text.DecimalFormat;
import java.util.*;

/**
 * easypoi framework
 *
 * @author xuzeyao
 */
@SuppressWarnings("all")
public class BaseExportService extends ExportCommonService implements ExportService {

    private int currentIndex = 0;

    private Workbook workbookTemp;

    protected IExcelExportStyler excelExportStyler;

    protected ExcelType type = ExcelType.HSSF;

    private Map<Integer, Double> statistics = new HashMap<>();

    private Map<Integer, CellStyle> cellStyleMap = new HashMap<>();

    private static final DecimalFormat DOUBLE_FORMAT = new DecimalFormat("######0.00");

    @Override
    public int[] createCells(Drawing patriarch, int index, Object t, List<ExcelExportEntity> excelParams, Sheet sheet, Workbook workbook, short rowHeight, int cellNum, List<String> alignmentList) {
        workbookTemp = workbook;
        try {
            ExcelExportEntity var1;
            Row row = sheet.getRow(index) == null ? sheet.createRow(index) : sheet.getRow(index);
            if (rowHeight != -1) {
                row.setHeight(rowHeight);
            }
            int var2 = 1;
            int margeCellNum = cellNum;
            int indexKey = createIndexCell(row, index, excelParams.get(0));
            cellNum += indexKey;
            for (int k = indexKey, paramSize = excelParams.size(); k < paramSize; k++) {
                var1 = excelParams.get(k);
                if (var1.getList() != null) {
                    Collection<?> list = getListCellValue(var1, t);
                    if (list != null && list.size() > 0) {
                        int tempCellNum = 0;
                        for (Object obj : list) {
                            int[] temp = createCells(patriarch, index + var2 - 1, obj, var1.getList(), sheet, workbook, rowHeight, cellNum, alignmentList);
                            tempCellNum = temp[1];
                            var2 += temp[0];
                        }
                        cellNum = tempCellNum;
                        var2 --;
                    }
                } else {
                    Object value = getCellValue(var1, t);
                    if (var1.getType() == BaseEntityTypeConstants.STRING_TYPE) {
                        CellStyle cellStyle;
                        if (cellStyleMap.get(cellNum) != null) {
                            cellStyle = cellStyleMap.get(cellNum);
                        } else {
                            cellStyle = workbook.createCellStyle();
                            cellStyleMap.put(cellNum, cellStyle);
                        }
                        cellStyle.setBorderBottom(BorderStyle.THIN);
                        cellStyle.setBorderLeft(BorderStyle.THIN);
                        cellStyle.setBorderTop(BorderStyle.THIN);
                        cellStyle.setBorderRight(BorderStyle.THIN);
                        if (alignmentList.get(cellNum) != null && "left".equals(alignmentList.get(cellNum))) {
                            cellStyle.setAlignment(HorizontalAlignment.LEFT);
                        } else if ("right".equals(alignmentList.get(cellNum))) {
                            cellStyle.setAlignment(HorizontalAlignment.RIGHT);
                        } else {
                            cellStyle.setAlignment(HorizontalAlignment.CENTER);
                        }
                        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

                        createStringCell(row, cellNum++, value == null ? "" : value.toString(), cellStyle, var1);
                        if (var1.isHyperlink()) {
                            row.getCell(cellNum - 1)
                                    .setHyperlink(dataHandler.getHyperlink(
                                            row.getSheet().getWorkbook().getCreationHelper(), t,
                                            var1.getName(), value));
                        }
                    } else if (var1.getType() == BaseEntityTypeConstants.DOUBLE_TYPE) {
                        createDoubleCell(row, cellNum ++, value == null ? "" : value.toString(),
                                index % 2 == 0 ? getStyles(false, var1) : getStyles(true, var1),
                                var1);
                        if (var1.isHyperlink()) {
                            row.getCell(cellNum - 1).setHyperlink(dataHandler.getHyperlink(
                                    row.getSheet().getWorkbook().getCreationHelper(), t,
                                    var1.getName(), value));
                        }
                    } else {
                        createImageCell(patriarch, var1, row, cellNum++,
                                value == null ? "" : value.toString(), t);
                    }
                }
            }
            for (int k = indexKey, paramSize = excelParams.size(); k < paramSize; k++) {
                var1 = excelParams.get(k);
                if (var1.getList() != null) {
                    margeCellNum += var1.getList().size();
                } else if (var1.isNeedMerge() && var2 > 1) {
                    for (int i = index + 1; i < index + var2; i++) {
                        sheet.getRow(i).createCell(margeCellNum);
                        sheet.getRow(i).getCell(margeCellNum).setCellStyle(getStyles(false, var1));
                    }
                    PoiMergeCellUtil.addMergedRegion(sheet, index, index + var2 - 1, margeCellNum, margeCellNum);
                    margeCellNum ++;
                }
            }
            return new int[] {var2, cellNum};
        } catch (Exception e) {
            ExportCommonService.LOGGER.error("Excel cell export error, data is: {}", ReflectionToStringBuilder.toString(t));
            ExportCommonService.LOGGER.error(e.getMessage(), e);
            throw new ExcelExportException(ExcelExportEnum.EXPORT_ERROR, e);
        }
    }

    @Override
    public void createImageCell(Drawing patriarch, ExcelExportEntity entity, Row row, int i, String imagePath, Object obj) throws Exception {
        Cell cell = row.createCell(i);
        byte[] value = null;
        if (entity.getExportImageType() != 1) {
            value = (byte[]) (entity.getMethods() != null
                    ? getFieldBySomeMethod(entity.getMethods(), obj)
                    : entity.getMethod().invoke(obj, new Object[]{}));
        }
        createImageCell(cell, 50 * entity.getHeight(), entity.getExportImageType() == 1 ? imagePath : null, value);
    }

    @Override
    public void createImageCell(Cell cell, double height, String imagePath, byte[] data) {
        if (height > cell.getRow().getHeight()) {
            cell.getRow().setHeight((short) height);
        }
        ClientAnchor var3;
        if (type.equals(ExcelType.HSSF)) {
            var3 = new HSSFClientAnchor(0, 0, 0, 0, (short) cell.getColumnIndex(), cell.getRow().getRowNum(), (short) (cell.getColumnIndex() + 1),
                    cell.getRow().getRowNum() + 1);
        } else {
            var3 = new XSSFClientAnchor(0, 0, 0, 0, (short) cell.getColumnIndex(), cell.getRow().getRowNum(), (short) (cell.getColumnIndex() + 1),
                    cell.getRow().getRowNum() + 1);
        }
        if (StringUtils.isNotEmpty(imagePath)) {
            data = ImageCache.getImage(imagePath);
        }
        if (data != null) {
            PoiExcelGraphDataUtil.getDrawingPatriarch(cell.getSheet()).createPicture(var3,
                    cell.getSheet().getWorkbook().addPicture(data, getImageType(data)));
        }
    }

    @Override
    public int createIndexCell(Row row, int index, ExcelExportEntity excelExportEntity) {
        if (excelExportEntity.getName() != null && "序号".equals(excelExportEntity.getName()) && excelExportEntity.getFormat() != null
                && excelExportEntity.getFormat().equals(PoiBaseConstants.IS_ADD_INDEX)) {
            createStringCell(row, 0, currentIndex + "",
                    index % 2 == 0 ? getStyles(false, null) : getStyles(true, null), null);
            currentIndex = currentIndex + 1;
            return 1;
        }
        return 0;
    }

    @Override
    public void createListCells(Drawing patriarch, int index, int cellNum, Object obj, List<ExcelExportEntity> excelParams, Sheet sheet, Workbook workbook, short rowHeight) throws Exception {
        ExcelExportEntity var1;
        Row var4;
        if (sheet.getRow(index) == null) {
            var4 = sheet.createRow(index);
            if (rowHeight != -1) {
                var4.setHeight(rowHeight);
            }
        } else {
            var4 = sheet.getRow(index);
            if (rowHeight != -1) {
                var4.setHeight(rowHeight);
            }
        }
        for (int k = 0, paramSize = excelParams.size(); k < paramSize; k++) {
            var1 = excelParams.get(k);
            Object value = getCellValue(var1, obj);
            if (var1.getType() == BaseEntityTypeConstants.STRING_TYPE) {
                createStringCell(var4, cellNum++, value == null ? "" : value.toString(),
                        var4.getRowNum() % 2 == 0 ? getStyles(false, var1) : getStyles(true, var1),
                        var1);
                if (var1.isHyperlink()) {
                    var4.getCell(cellNum - 1)
                            .setHyperlink(dataHandler.getHyperlink(
                                    var4.getSheet().getWorkbook().getCreationHelper(), obj, var1.getName(),
                                    value));
                }
            } else if (var1.getType() == BaseEntityTypeConstants.DOUBLE_TYPE) {
                createDoubleCell(var4, cellNum++, value == null ? "" : value.toString(),
                        index % 2 == 0 ? getStyles(false, var1) : getStyles(true, var1), var1);
                if (var1.isHyperlink()) {
                    var4.getCell(cellNum - 1)
                            .setHyperlink(dataHandler.getHyperlink(
                                    var4.getSheet().getWorkbook().getCreationHelper(), obj, var1.getName(),
                                    value));
                }
            } else {
                createImageCell(patriarch, var1, var4, cellNum++,
                        value == null ? "" : value.toString(), obj);
            }
        }
    }

    @Override
    public void createStringCell(Row row, int index, String text, CellStyle style, ExcelExportEntity entity) {
        Cell cell = row.createCell(index);
        if (style != null && style.getDataFormat() > 0 && style.getDataFormat() < 12) {
            cell.setCellValue(Double.parseDouble(text));
            cell.setCellType(CellType.NUMERIC);
        } else {
            RichTextString var5;
            if (type.equals(ExcelType.HSSF)) {
                var5 = new HSSFRichTextString(text);
            } else {
                var5 = new XSSFRichTextString(text);
            }
            cell.setCellValue(var5);
        }
        if (style != null) {
            cell.setCellStyle(style);
        }
        addStatisticsData(index, text, entity);
    }

    @Override
    public void createDoubleCell(Row row, int index, String text, CellStyle style, ExcelExportEntity entity) {
        Cell cell = row.createCell(index);
        if (text != null && text.length() > 0) {
            cell.setCellValue(Double.parseDouble(text));
        }
        cell.setCellType(CellType.NUMERIC);
        if (style != null) {
            cell.setCellStyle(style);
        }
        addStatisticsData(index, text, entity);
    }

    @Override
    public void addStatisticsRow(CellStyle styles, Sheet sheet) {
        if (statistics.size() > 0) {
            if (ExportCommonService.LOGGER.isDebugEnabled()) {
                ExportCommonService.LOGGER.debug("Add statistics data, size is {}", statistics.size());
            }
            Row row = sheet.createRow(sheet.getLastRowNum() + 1);
            Set<Integer> keys = statistics.keySet();
            createStringCell(row, 0, "合计", styles, null);
            for (Integer key : keys) {
                createStringCell(row, key, DOUBLE_FORMAT.format(statistics.get(key)), styles, null);
            }
            statistics.clear();
        }
    }

    @Override
    public void addStatisticsData(Integer index, String text, ExcelExportEntity entity) {
        if (entity != null && entity.isStatistics()) {
            Double var6 = 0D;
            if (!statistics.containsKey(index)) {
                statistics.put(index, var6);
            }
            try {
                var6 = Double.valueOf(text);
            } catch (NumberFormatException e) {
            }
            statistics.put(index, statistics.get(index) + var6);
        }
    }

    @Override
    public int getImageType(byte[] value) {
        String type = PoiPublicUtil.getFileExtendName(value);
        if ("JPG".equalsIgnoreCase(type)) {
            return Workbook.PICTURE_TYPE_JPEG;
        } else if ("PNG".equalsIgnoreCase(type)) {
            return Workbook.PICTURE_TYPE_PNG;
        }
        return Workbook.PICTURE_TYPE_JPEG;
    }

    @Override
    public Map<Integer, int[]> getMergeDataMap(List<ExcelExportEntity> excelParams) {
        Map<Integer, int[]> mergeMap = new HashMap<Integer, int[]>();
        // 设置参数顺序,为之后合并单元格做准备
        int var7 = 0;
        for (ExcelExportEntity entity : excelParams) {
            if (entity.isMergeVertical()) {
                mergeMap.put(var7, entity.getMergeRely());
            }
            if (entity.getList() != null) {
                for (ExcelExportEntity inner : entity.getList()) {
                    if (inner.isMergeVertical()) {
                        mergeMap.put(var7, inner.getMergeRely());
                    }
                    var7 ++;
                }
            } else {
                var7 ++;
            }
        }
        return mergeMap;
    }

    @Override
    public CellStyle getStyles(boolean needOne, ExcelExportEntity entity) {
        return excelExportStyler.getStyles(needOne, entity);
    }

    @Override
    public void mergeCells(Sheet sheet, List<ExcelExportEntity> excelParams, int titleHeight) {
        Map<Integer, int[]> mergeMap = getMergeDataMap(excelParams);
        PoiMergeCellUtil.mergeCells(sheet, mergeMap, titleHeight);
    }

    @Override
    public void setCellWidth(List<ExcelExportEntity> excelParams, Sheet sheet) {
        int var7 = 0;
        for (int i = 0; i < excelParams.size(); i ++) {
            if (excelParams.get(i).getList() != null) {
                List<ExcelExportEntity> list = excelParams.get(i).getList();
                for (int j = 0; j < list.size(); j++) {
                    sheet.setColumnWidth(var7, (int) (256 * list.get(j).getWidth()));
                    var7 ++;
                }
            } else {
                sheet.setColumnWidth(var7, (int) (256 * excelParams.get(i).getWidth()));
                var7 ++;
            }
        }
    }

    @Override
    public void setColumnHidden(List<ExcelExportEntity> excelParams, Sheet sheet) {
        int var7 = 0;
        for (int i = 0; i < excelParams.size(); i++) {
            if (excelParams.get(i).getList() != null) {
                List<ExcelExportEntity> list = excelParams.get(i).getList();
                for (int j = 0; j < list.size(); j++) {
                    sheet.setColumnHidden(var7, list.get(j).isColumnHidden());
                    var7 ++;
                }
            } else {
                sheet.setColumnHidden(var7, excelParams.get(i).isColumnHidden());
                var7 ++;
            }
        }
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public void setExcelExportStyler(IExcelExportStyler excelExportStyler) {
        this.excelExportStyler = excelExportStyler;
    }

    public IExcelExportStyler getExcelExportStyler() {
        return excelExportStyler;
    }

}
