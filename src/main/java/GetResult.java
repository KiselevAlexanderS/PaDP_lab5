public class GetResult {
    private String url;
    private int count;

    public GetResult(String url, int count) {
        this.count = count;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public int getCount() {
        return count;
    }
}
