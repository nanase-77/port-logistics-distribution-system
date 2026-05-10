package com.smu.portlogisticsdistributionsystem.util;

public class RedisConstants {
    public static final Long LOGIN_CODE_TTL = 2L;
    public static final String LOGIN_SESSION = "plds:user:session:";
    public static final Long LOGIN_USER_TTL = 36000L;
    public static final String COMPANY = "plds:company:";

    public static final String ORDERS_CACHE = "plds:orders:";
    public static final String LOGISTICS_CACHE = "plds:logistics:";

    public static final Long ORDERS_TTL_MIN = 1800L;
    public static final Long ORDERS_TTL_MAX = 3600L;
    public static final Long LOGISTICS_TTL_MIN = 1800L;
    public static final Long LOGISTICS_TTL_MAX = 3600L;
}