package com.aiyichen.admindemo.enums;

/**
 * 队伍状态枚举
 */
public enum TeamStatusEnum {
    PUBLIC(0,"公开"),
    PRIVATE(1,"私有"),
    SECRET(2,"加密");

    private int value;
    private String text;
    // 根据数字获得状态 状态包括数字和描述
    public static TeamStatusEnum getEnumbyValue(Integer value){
        if (value == null){
            return null;
        }
        TeamStatusEnum[] values = TeamStatusEnum.values();
        for (TeamStatusEnum teamStatusEnum : values){
            if (teamStatusEnum.getValue() == value){
                return teamStatusEnum;
            }
        }
        return null;
    }
    // 构造函数 自动生成
    TeamStatusEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
