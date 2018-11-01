package com.dalimao.mytaxi.common.http.api;

public class Api {
    public static final String TEST_GET = "/get?uid=${uid}";
    public static final String TEST_POST = "/post";
    public static final String GET_VERIFY_URL = "f34e28da5816433d/getMsgCode?phone=${phone} ";

    public static class Config {
        private static final String TEST_DOMAIN = "http://cloud.bmob.cn/";//HTTP://httpbin.org
        private static final String RELEASE_DOMAIN = "http://cloud.bmob.cn/";
        private static final String RELEASE_APPLICATION_KEY = "";
        private static final String DEBUG_APPLICATION_KEY = "";
        private static final String RELEASE_APP_KEY = "";
        private static final String DEBUG_APP_KEY = "";
        private static String domain = TEST_DOMAIN;
        private static String applicationKey = DEBUG_APPLICATION_KEY;
        private static String appKey = DEBUG_APP_KEY;

        public static String getDomain() {
            return domain;
        }

        public static void setDebug(boolean debug) {
            domain = debug ? TEST_DOMAIN : RELEASE_DOMAIN;
            applicationKey = debug ? DEBUG_APPLICATION_KEY : RELEASE_APPLICATION_KEY;
            appKey = debug ? DEBUG_APP_KEY : RELEASE_APP_KEY;
        }
    }
}
