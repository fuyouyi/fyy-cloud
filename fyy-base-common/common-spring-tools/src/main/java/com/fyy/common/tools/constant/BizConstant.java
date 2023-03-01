package com.fyy.common.tools.constant;

/**
 * 业务常用常量
 *
 * @author lvbin
 * @since 2019/12/7
 */
public interface BizConstant {

    Integer INVALID = -1;
    Integer YES = 1;
    Integer NO = 0;

    /** 一天小时数 */
    Integer DAY_OF_HOURS = 24;

    /** 一小时分钟数 */
    Integer HOUR_OF_MINUTES = 60;

    /** 开始时间 */
    String START_TIME = "startTime";
    /** 结束时间 */
    String END_TIME = "endTime";

    String COMMA = ",";
    char COMMA_CHAR = ',';
    String UNDERLINE = "_";

    /**
     * 冒号
     */
    String COLON = ":";

    /**
     * 空格
     */
    String BLANK_SPACE = " ";

    /**
     * 斜线
     */
    String SLANT = "/";
    /**
     * 分割符号，这个是特殊字符，一般情况下用户是没法输入的。除非用户故意找到这个字符。
     * ！！！已经使用，禁止修改。
     */
    String SPLIT_STR = "⇞";
    Integer ZERO = 0;
    Integer ONE = 1;
    Integer TWO = 2;
    Integer NEGATIVE_ONE = -1;
    Integer THREE = 3;
    String THREE_STR = "3";
    Integer SEVEN = 7;
    Integer HUNDRED = 100;
    Long ZERO_LONG = 0L;
    Long ONE_LONG = 1L;
    Double ZERO_DOUBLE = 0D;
    String EMPTY_ARRAY = "[]";
    Long MINUS_ONE_LONG = -1L;
    String ZERO_PERCENTAGE = "0.00%";
}
