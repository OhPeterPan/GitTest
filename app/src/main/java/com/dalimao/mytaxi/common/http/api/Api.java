package com.dalimao.mytaxi.common.http.api;

public class Api {
    public static final String TEST_GET = "/get?uid=${uid}";
    public static final String TEST_POST = "/post";

    public static class Config {
        private static final String TEST_DOMAIN = "HTTP://httpbin.org";
        private static final String RELEASE_DOMAIN = "HTTP://httpbin.org";
        private static String domain = TEST_DOMAIN;

        public static String getDomain() {
            return domain;
        }

        public static void setDebug(boolean debug) {
            domain = debug ? TEST_DOMAIN : RELEASE_DOMAIN;
        }
    }
}
