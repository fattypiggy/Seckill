package org.seckill.enums;

/**
 * 枚举类型 字典库
 * Created by williamjing on 2017/2/23.
 */
public enum SeckillStateEnum {
    SUCCESS(1,"秒杀成功"),
    SCEKILL_CLOSED(0,"秒杀关闭"),
    INNER_ERROR(-1,"内部错误"),
    END(-2,"秒杀结束"),
    DATA_OVERWRITE(-3," 数据篡改");

    private int state;

    private String stateInfo;

    SeckillStateEnum(int state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
    }

    public int getState() {
        return state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public static SeckillStateEnum stateOf(int index){
        for (SeckillStateEnum state:values()) {
            if (index == state.getState()){
                return state;
            }
        }
        return null;
    }
}
