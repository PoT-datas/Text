package api.pot.text.linkpreview;

public interface ResponseListener {

    void onData(MetaData metaData);

    void onError(Exception e);
}
