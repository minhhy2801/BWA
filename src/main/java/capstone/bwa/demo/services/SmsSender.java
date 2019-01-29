package capstone.bwa.demo.services;

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SmsSender {
    public static final String API_URL = "https://api.speedsms.vn/index.php";
    protected String mAccessToken;

    public SmsSender(String accessToken) {
        this.mAccessToken = accessToken;
    }

    //    public static String sendSmsToCreateAccount(String phone) {
//        String msg = "";
//        int verificationCode = new Random().nextInt(9999 - 1000) + 1000;
//        System.out.println(verificationCode);
//        try {
//            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
//            Message message = Message.creator(
//                    new PhoneNumber(phone),
//                    new PhoneNumber("+16196481538"),
//                    "Your BWA Verification Code is: " + verificationCode)
//                    .create();
//            System.out.println(message.getSid());
//            msg = verificationCode + "";
//        } catch (Exception e) {
//            msg = "Số điện thoại không hợp lệ";
//        }
//        return msg;
//    }

    /**
     * sms_type có các giá trị như sau:
     * 2: tin nhắn gửi bằng đầu số ngẫu nhiên
     * 3: tin nhắn gửi bằng brandname
     * 4: tin nhắn gửi bằng brandname mặc định (Verify hoặc Notify)
     * 5: tin nhắn gửi bằng app android
     */
    public String sendSmsToCreateAccount(String to, String content, int type, String sender) throws IOException {
        String json = "{\"to\": [\"" + to + "\"], \"content\": \"" + EncodeNonAsciiCharacters(content)
                + "\", \"type\":" + type + ", \"brandname\":\"" + sender + "\"}";
        URL url = new URL(API_URL + "/sms/send");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        String userCredentials = mAccessToken + ":x";
        String basicAuth = "Basic " + DatatypeConverter.printBase64Binary(userCredentials.getBytes());
        conn.setRequestProperty("Authorization", basicAuth);
        conn.setRequestProperty("Content-Type", "application/json");

        conn.setDoOutput(true);

        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.writeBytes(json);
        wr.flush();
        wr.close();

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine = "";
        StringBuffer buffer = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            buffer.append(inputLine);
        }
        in.close();
        return buffer.toString();
    }

    private String EncodeNonAsciiCharacters(String value) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            int unit = (int) c;
            if (unit > 127) {
                String hex = String.format("%04x", (int) unit);
                String encodedValue = "\\u" + hex;
                sb.append(encodedValue);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
