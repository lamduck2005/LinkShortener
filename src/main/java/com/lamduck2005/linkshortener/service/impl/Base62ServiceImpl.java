package com.lamduck2005.linkshortener.service.impl;

import com.lamduck2005.linkshortener.service.Base62Service;
import org.springframework.stereotype.Service;

@Service
public class Base62ServiceImpl implements Base62Service {

    // 62 ký tự: 0-9 (10), a-z (26), A-Z (26)
    private static final String BASE62_CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = BASE62_CHARS.length(); // BASE = 62

    /**
     * Mã hóa ID số thành chuỗi Base62.
     */
    @Override
    public String encode(long number) {
        if (number == 0) {
            return String.valueOf(BASE62_CHARS.charAt(0));
        }

        StringBuilder sb = new StringBuilder();
        // Thuật toán: Chia lấy dư theo cơ số 62
        while (number > 0) {
            // Lấy ký tự tại vị trí (number % 62)
            sb.insert(0, BASE62_CHARS.charAt((int)(number % BASE)));
            number /= BASE;
        }
        return sb.toString();
    }

    /**
     * Giải mã chuỗi Base62 thành ID số.
     */
    @Override
    public long decode(String base62String) {
        long number = 0;
        // Thuật toán: Nhân với cơ số 62 và cộng dồn
        for (char character : base62String.toCharArray()) {
            number = number * BASE + BASE62_CHARS.indexOf(character);
        }
        return number;
    }
}