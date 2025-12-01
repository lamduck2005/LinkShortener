package com.lamduck2005.linkshortener.service;

public interface Base62Service {
    String encode(long number); // Chuyển ID số thành mã Base62
    long decode(String base62String); // Chuyển mã Base62 thành ID số
}