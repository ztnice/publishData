package bean;


import java.io.Serializable;

/**
 * @Author: haozt
 * @Date: 2018/7/25
 * @Description:
 */
public class OperateResult implements Serializable {
    private static final long serialVersionUID = -3456789098754323234L;

    public static final String SUCCESS_MSG ="数据发放成功！";
    public static final String FAILED_MSG ="数据发放失败！";
    public static final Long SUCCESS_CODE = 1000L;
    public static final Long FAILED_CODE = 1001L;
    /**
     * 错误信息
     */
    private String errMsg;
    /**
     * 错误代码
     */
    private Long errCode;


    public static boolean isSuccess(OperateResult dto) {

        if(dto != null && Long.valueOf(1000L).equals(dto.getErrCode())) {

            return true;
        }
        return false;
    }

    public  String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public Long getErrCode() {
        return errCode;
    }

    public void setErrCode(Long errCode) {
        this.errCode = errCode;
    }

    public static OperateResult getSuccessResult(){
        OperateResult respDTO = new OperateResult();
        respDTO.setErrMsg(SUCCESS_MSG);
        respDTO.setErrCode(SUCCESS_CODE);
        return respDTO;
    }

    public static OperateResult getFailResult(){
        OperateResult respDTO = new OperateResult();
        respDTO.setErrMsg(FAILED_MSG);
        respDTO.setErrCode(FAILED_CODE);
        return respDTO;
    }

}
