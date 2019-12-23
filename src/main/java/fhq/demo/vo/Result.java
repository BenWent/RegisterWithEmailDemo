package fhq.demo.vo;

/**
 * @author fhq
 * @date 2019/12/20 10:49
 */
public class Result<T> {
    private CodeMsg codeMsg;
    private T data;

    public Result(T data, CodeMsg codeMsg) {
        this.codeMsg = codeMsg;
        this.data = data;
    }

    public Result(CodeMsg codeMsg) {
        this.codeMsg = codeMsg;
    }

    public int getCode() {
        return codeMsg.getCode();
    }

    public String getMsg() {
        return codeMsg.getMsg();
    }

    public T getData() {
        return data;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(data, CodeMsg.SUCCESS);
    }

    public static <T> Result<T> error(CodeMsg codeMsg) {
        return new Result<>(codeMsg);
    }
}
