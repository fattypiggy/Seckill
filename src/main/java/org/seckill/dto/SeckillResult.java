package org.seckill.dto;

/**
 * 所有ajax返回数据  封装json结果
 * Created by williamjing on 2017/2/25.
 */
public class SeckillResult<T> {
    private boolean success;

    private T data;

    private String errorMsg;

    public SeckillResult(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    public SeckillResult(boolean success, String errorMsg) {
        this.success = success;
        this.errorMsg = errorMsg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
