package com.dalimao.mytaxi.common.http.api;

public class Api {
    public static final String TEST_GET = "/get?uid=${uid}";
    public static final String TEST_POST = "/post";
    public static final String GET_VERIFY_URL = "f34e28da5816433d/getMsgCode?phone=${phone}";
    public static final String CHECK_VERIFY_URL = "f34e28da5816433d/checkMsgCode?phone=${phone}&code=${code}";
    public static final String CHECK_REGISTER_URL = "f34e28da5816433d/isUserExist?phone=${phone}";
    public static final String REGISTER_URL = "f34e28da5816433d/register";
    public static final String AUTH_URL = "f34e28da5816433d/auth";
    public static final String LOGIN_URL = "f34e28da5816433d/login";
    public static final String NEAR_DIV_URL = "f34e28da5816433d/getNearDrivers?latitude=${latitude}&longitude=${longitude}";
    public static final String UPLOAD_LOCATION_URL = "f34e28da5816433d/updateUserLocation";
    public static final String CALL_DRIVER_URL = "f34e28da5816433d/callDriver";
    public static final String CANCEL_ORDER_URL = "f34e28da5816433d/cancelOrder";

    public static class Config {
        private static final String TEST_DOMAIN = "http://cloud.bmob.cn/";//HTTP://httpbin.org
        private static final String RELEASE_DOMAIN = "http://cloud.bmob.cn/";
        private static final String RELEASE_APPLICATION_KEY = "e90928398db0130b0d6d21da7bde357e";
        private static final String DEBUG_APPLICATION_KEY = "e90928398db0130b0d6d21da7bde357e";
        private static final String RELEASE_APP_KEY = "514d8f8a2371bdf1566033f6664a24d2";
        private static final String DEBUG_APP_KEY = "514d8f8a2371bdf1566033f6664a24d2";
        private static String domain = TEST_DOMAIN;
        private static String applicationKey = DEBUG_APPLICATION_KEY;
        private static String appKey = DEBUG_APP_KEY;

        public static String getDomain() {
            return domain;
        }

        public static String getApplicationKey() {
            return applicationKey;
        }

        public static String getAppKey() {
            return appKey;
        }

        public static void setDebug(boolean debug) {
            domain = debug ? TEST_DOMAIN : RELEASE_DOMAIN;
            applicationKey = debug ? DEBUG_APPLICATION_KEY : RELEASE_APPLICATION_KEY;
            appKey = debug ? DEBUG_APP_KEY : RELEASE_APP_KEY;
        }
    }
}
