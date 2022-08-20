package api.pot.text.xtv.tools;

public interface SmartTextCallbackInter {

    void hashTagClick(String hashTag);
    void mentionClick(String mention);
    void emailClick(String email);
    void phoneNumberClick(String phoneNumber);
    void webUrlClick(String webUrl);
    void geoPosClick(double lat, double lng);
    void regexClick(String value);
}
