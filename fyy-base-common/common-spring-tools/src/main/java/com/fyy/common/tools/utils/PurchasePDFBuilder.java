package com.fyy.common.tools.utils;

import com.alibaba.fastjson2.JSONObject;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.fyy.common.tools.exception.RenException;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class PurchasePDFBuilder {

    private final String DEFAULT_CURRENCY = "$ ";

    /**
     * pdf内容元素列表
     */
    private final List<IBlockElement> elementList;

    public List<IBlockElement> getElementList() {
        return elementList;
    }

    /**
     * pdf生成路径
     */
    private String filePath;

    /**
     * 字体大小
     */
    private int fontSize;

    public static void main(String[] args) throws Exception {
        String filePath = "PURCHASE_PRICE" + "/" + 213241L + ".pdf";

        Map<String, String> priceBaseInfoMap = new LinkedHashMap<>();
        priceBaseInfoMap.put("供应商名称", "宇宙供应商");
        priceBaseInfoMap.put("采购询盘名称", "采购云朵");
        priceBaseInfoMap.put("供应时间", "2022-10-22 10:15:00");
        priceBaseInfoMap.put("报价认证", "是");
        priceBaseInfoMap.put("报价总额", "$156");

        List<String> headerList = new ArrayList<>();
        headerList.add("序号");
        headerList.add("商品名称");
        headerList.add("价格");
        headerList.add("单位");
        headerList.add("数量");
        headerList.add("备注");

        List<JSONObject> priceList = new ArrayList<>();

        PurchasePDFBuilder.builder()
                .setFilePath(filePath)
                .setFontSize(13)
                .addTitle("报价函")
                .addNoBorderTable(priceBaseInfoMap)
                .addItemTitle("报价表")
                .addTableForPurchasePrice(new float[]{1, 3, 1, 1, 1, 3}, headerList, priceList )
                .build();
    }


    public static PurchasePDFBuilder builder() {
        return new PurchasePDFBuilder();
    }

    public PurchasePDFBuilder setFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    public PurchasePDFBuilder setFontSize(int fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    /**
     * 添加pdf文件标题，居中粗体
     *
     * @param title 标题
     */
    public PurchasePDFBuilder addTitle(String title) {
        Paragraph titleElement = new Paragraph(title)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(20f)
                .setBold();
        this.elementList.add(titleElement);
        return this;
    }

    /**
     * 添加子标题
     *
     * @param itemTitle 子标题
     */
    public PurchasePDFBuilder addItemTitle(String itemTitle) {
        Paragraph itemTitleElement = new Paragraph(itemTitle).setBold();
        this.elementList.add(itemTitleElement);
        return this;
    }

    /**
     * 添加无框表格
     *
     * @param tableMap   表格内容
     */
    public PurchasePDFBuilder addNoBorderTable(Map<String, String> tableMap) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{1.4f, 2, 1, 2, 2, 0}));
        for (Map.Entry<String, String> entry : tableMap.entrySet()) {
            table.addCell(noBorderCell(entry.getKey() + "："));
            table.addCell(noBorderCell(entry.getValue()));
            table.addCell(noBorderCell(""));
        }
        this.elementList.add(table);
        // 表格后面都空一行
        this.elementList.add(new Paragraph().setHeight(15f));
        return this;
    }

    /**
     * 添加无表头表格
     *
     * @param tableValue 表格比例
     * @param tableMap   表格内容
     */
    public PurchasePDFBuilder addNoHeaderTable(float[] tableValue, Map<String, String> tableMap) {
        Table table = new Table(UnitValue.createPercentArray(tableValue));
        tableContent(tableMap, table);
        return this;
    }

    /**
     * 添加采购单表格
     *
     * @param tableValue 比例
     * @param headerList 表头
     * @param orderList  采购单列表
     */
    public PurchasePDFBuilder addTableForPurchaseOrder(float[] tableValue, List<String> headerList, List<JSONObject> orderList) {
        Table table = new Table(UnitValue.createPercentArray(tableValue));
        // 表头
        for (String header : headerList) {
            table.addHeaderCell(new Cell().add(new Paragraph(header))
                    .setBold()
                    .setBackgroundColor(Color.GRAY, 0.3F));
        }
        int num = 1;
        for (JSONObject detailDTO : orderList) {
            table.addCell(new Cell().add(new Paragraph(num++ + "")));
            table.addCell(new Cell().add(new Paragraph("xx商品")));
            table.addCell(new Cell().add(new Paragraph("xx规格")));
            table.addCell(new Cell().add(new Paragraph("yy个")));
            table.addCell(new Cell().add(new Paragraph("哈哈")));
        }
        this.elementList.add(table);
        // 表格后面都空一行
        this.elementList.add(new Paragraph().setHeight(15f));
        return this;
    }

    /**
     * 添加销售考核表格
     *
     * @param tableValue 比例
     * @param headerList 表头
     * @param priceList  报价单列表
     */
    public PurchasePDFBuilder addTableForSaleTask(float[] tableValue, List<String> headerList, List<JSONObject> priceList) {
        Table table = new Table(UnitValue.createPercentArray(tableValue));
        try {
            for (JSONObject detailDTO : priceList) {
                table.addCell(new Cell().add(new Paragraph("")));
                table.addCell(new Cell().add(new Paragraph("x个")));
                // 若是加密报价则需使用加密字段进行解密
                table.addCell(new Cell().add(new Paragraph(DEFAULT_CURRENCY + "156")));
                table.addCell(new Cell().add(new Paragraph("xx规格")));
                table.addCell(new Cell().add(new Paragraph("yy个")));
                table.addCell(new Cell().add(new Paragraph("哈哈")));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RenException("数据加密异常");
        }
        this.elementList.add(table);
        // 表格后面都空一行
        this.elementList.add(new Paragraph().setHeight(15f));
        return this;
    }

    /**
     * 添加报价单表格
     *
     * @param tableValue 比例
     * @param headerList 表头
     * @param priceList  报价单列表
     */
    public PurchasePDFBuilder addTableForPurchasePrice(float[] tableValue, List<String> headerList, List<JSONObject> priceList) {
        Table table = new Table(UnitValue.createPercentArray(tableValue));
        // 表头
        for (String header : headerList) {
            table.addHeaderCell(new Cell().add(new Paragraph(header))
                    .setBold()
                    .setBackgroundColor(Color.GRAY, 0.3F));
        }
        int num = 1;
        try {
            for (JSONObject detailDTO : priceList) {
                table.addCell(new Cell().add(new Paragraph(num++ + "")));
                table.addCell(new Cell().add(new Paragraph("x个")));
                // 若是加密报价则需使用加密字段进行解密
                table.addCell(new Cell().add(new Paragraph(DEFAULT_CURRENCY + "156")));
                table.addCell(new Cell().add(new Paragraph("xx规格")));
                table.addCell(new Cell().add(new Paragraph("yy个")));
                table.addCell(new Cell().add(new Paragraph("哈哈")));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RenException("数据加密异常");
        }
        this.elementList.add(table);
        // 表格后面都空一行
        this.elementList.add(new Paragraph().setHeight(15f));
        return this;
    }

    private PurchasePDFBuilder() {
        this.elementList = new ArrayList<>();
    }

    public void build() throws Exception {

        Files.createDirectories(Paths.get(filePath.substring(0, filePath.lastIndexOf("/"))));
        PdfWriter pdfWriter = new PdfWriter(filePath);
        PdfDocument pdf = new PdfDocument(pdfWriter);
        Document document = new Document(pdf);

        // 创建中文字体
        PdfFont chineseFont = PdfFontFactory.createFont("STSong-Light", "UniGB-UCS2-H");
        // 全局字体设置
        document.setFont(chineseFont).setFontSize(fontSize);

        for (IBlockElement element : elementList) {
            document.add(element);
        }

        document.close();
        pdfWriter.close();
    }

    private Cell noBorderCell(String text) {
        return new Cell().add(new Paragraph(text)).setBorder(Border.NO_BORDER);
    }

    private void tableContent(Map<String, String> tableMap, Table table) {
        for (Map.Entry<String, String> entry : tableMap.entrySet()) {
            table.addCell(new Cell().add(new Paragraph(entry.getKey() != null ? entry.getKey() : "")));
            table.addCell(new Cell().add(new Paragraph(entry.getValue() != null ? entry.getValue() : "")));
        }
        this.elementList.add(table);
        // 表格后面都空一行
        this.elementList.add(new Paragraph().setHeight(15f));
    }
}