public class ResponseResult {
    private boolean isEmpty;
    private String url;
    private Long time;

    ResponseResult(boolean isEmpty, String url, Long time) {
        this.isEmpty = isEmpty;
        this.url = url;
        this.time = time;
    }
}
