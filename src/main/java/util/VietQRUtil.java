package util;

import javax.servlet.ServletContext;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Builds VietQR image URL (img.vietqr.io) for bank transfer QR with amount.
 * Configure in web.xml context-param: vietqr_acq_id (6 digits or bank short name like Vietinbank),
 * vietqr_account_no, vietqr_account_name.
 */
public final class VietQRUtil {

    private static final String BASE = "https://img.vietqr.io/image";
    private static final String TEMPLATE = "compact2";

    /**
     * Build VietQR image URL for the given amount (VND) and description.
     * addInfo is used as transfer content (max 25 chars, no diacritics recommended).
     */
    public static String getPaymentURL(ServletContext ctx, long amountVND, String addInfo) {
        String bankId = ctx.getInitParameter("vietqr_acq_id");
        String accountNo = ctx.getInitParameter("vietqr_account_no");
        String accountName = ctx.getInitParameter("vietqr_account_name");
        if (bankId == null || accountNo == null) {
            return null;
        }
        if (accountName == null) accountName = "";
        if (addInfo == null) addInfo = "";
        addInfo = normalizeForVietQR(addInfo);
        if (addInfo.length() > 25) addInfo = addInfo.substring(0, 25);
        try {
            String encodedName = URLEncoder.encode(accountName, StandardCharsets.UTF_8.name());
            String encodedInfo = URLEncoder.encode(addInfo, StandardCharsets.UTF_8.name());
            // Bank ID: 6-digit BIN (e.g. 970415) or short name (e.g. Vietinbank). Use .png for compatibility.
            String bank = bankId.trim();
            String ext = "png";
            return String.format("%s/%s-%s-%s.%s?amount=%d&addInfo=%s&accountName=%s",
                    BASE, bank, accountNo.trim(), TEMPLATE, ext,
                    amountVND, encodedInfo, encodedName);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    private static String normalizeForVietQR(String s) {
        if (s == null) return "";
        return s.replaceAll("[^\\x00-\\x7F]", "").trim();
    }
}
