package enums;

/**
 * @Classname DelEnum
 * @Description
 * @Date 2023/10/27 17:06
 * @Created by lth
 */
public enum DelEnum {
    NORMAL(0, "正常"),
    DELETE(1, "删除");

    DelEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
    private Integer code;
    private String message;

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static DelEnum getEnumByCode(Integer code) {
        for (DelEnum delEnum : DelEnum.values()) {
            if (delEnum.getCode().equals(code)) {
                return delEnum;
            }
        }
        return null;
    }
}
