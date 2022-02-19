public class StoreResult {
    private String url;
    private long time;

    StoreResult(String url, long time) {
        this.time = time;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public long getTime() {
        return time;
    }
}
