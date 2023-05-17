package tk.jandev.minecraftsessionlogin.client.sessionLogin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.network.encryption.PlayerPublicKey;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;

public class GetPublicKey {

    public static String publicKeyString;
    public static PublicKey publicKey;
    public static PlayerPublicKey.PublicKeyData publicKeyData;

    public static String privateKeyString;
    public static PrivateKey privateKey;

    public static Instant expiresAt;
    public static Instant refreshedAfter;

    public static byte[] keySignature;
    public static byte[] keySignatureV2;

    public static void requestKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

/*
        POST https://api.minecraftservices.com/player/certificates

        Headers:
        Authorization: Bearer <access token>

        Response:

        {
          "keyPair": {
            "privateKey": "-----BEGIN RSA PRIVATE KEY-----\n ... \n-----END RSA PRIVATE KEY-----\n",
            "publicKey": "-----BEGIN RSA PUBLIC KEY-----\n ... \n-----END RSA PUBLIC KEY-----\n"
          },
          "publicKeySignature": "[base64 string; signed data]",
          "publicKeySignatureV2": "[base64 string; signed data]",
          "expiresAt": "2022-04-30T00:11:32.174783069Z",
          "refreshedAfter": "2022-04-29T16:11:32.174783069Z"
        }
*/

        String token = SetSession.accessToken;

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("https://api.minecraftservices.com/player/certificates");

        // Request parameters and other properties.
        httppost.setEntity(new StringEntity("", ContentType.create("application/json", "UTF-8")));
        httppost.addHeader("Authorization", "Bearer " + token);

        //Execute and get the response.
        CloseableHttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();
        System.out.println(response.getProtocolVersion() + " Code:" + response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase());

        InputStream inputStream = entity.getContent();


        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(
                new InputStreamReader(inputStream, "UTF-8"));

        refreshedAfter = Instant.parse(jsonObject.get("refreshedAfter").getAsString());

        expiresAt = Instant.parse(jsonObject.get("expiresAt").getAsString());

        keySignature = Base64.getDecoder().decode(jsonObject.get("publicKeySignature").getAsString());
        keySignatureV2 = Base64.getDecoder().decode(jsonObject.get("publicKeySignatureV2").getAsString());

        JsonObject keyPair = (JsonObject) jsonObject.get("keyPair");

        privateKeyString = keyPair.get("privateKey").getAsString()
                .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "");

        X509EncodedKeySpec privSpec = new X509EncodedKeySpec(Base64.getDecoder().decode(privateKeyString));
        KeyFactory privKf = KeyFactory.getInstance("RSA");
        privateKey = privKf.generatePrivate(privSpec);


        publicKeyString = keyPair.get("publicKey").getAsString();
        X509EncodedKeySpec publSpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyString));
        KeyFactory publKf = KeyFactory.getInstance("RSA");
        publicKey = publKf.generatePublic(publSpec);

        publicKeyData = new PlayerPublicKey.PublicKeyData(expiresAt, publicKey, keySignature);

        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
    }
}
