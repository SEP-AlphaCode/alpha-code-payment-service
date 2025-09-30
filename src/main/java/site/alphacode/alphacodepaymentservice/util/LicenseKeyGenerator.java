package site.alphacode.alphacodepaymentservice.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

@Component
public class LicenseKeyGenerator {
    private final String secret;

    public LicenseKeyGenerator(@Value("${license.key.secret}") String secret) {
        this.secret = secret;
    }

    public String generate(UUID accountId) {
        try {
            // Dữ liệu muốn nhúng trong key
            String data = accountId + "|" + System.currentTimeMillis();

            // Sinh chữ ký HMAC-SHA256
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmac.init(keySpec);

            byte[] signature = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            // Kết hợp data + signature
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString((data + "|" + HexFormat.of().formatHex(signature)).getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("Error generating license key", e);
        }
    }

    public boolean verify(String licenseKey) {
        try {
            String decoded = new String(Base64.getUrlDecoder().decode(licenseKey), StandardCharsets.UTF_8);

            // Format: accountId|timestamp|signature
            String[] parts = decoded.split("\\|");
            if (parts.length != 3) return false;

            String data = parts[0] + "|" + parts[1];
            String signatureHex = parts[2];

            // Tính lại signature
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmac.init(keySpec);

            byte[] expectedSig = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            String expectedSigHex = HexFormat.of().formatHex(expectedSig);

            // So sánh
            return expectedSigHex.equals(signatureHex);
        } catch (Exception e) {
            return false;
        }
    }
}
