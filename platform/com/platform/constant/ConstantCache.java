package com.platform.constant;

/**
 * 缓存
 */
public interface ConstantCache {

	/**
	 * 缓存类型ehcache
	 */
	public static final String cache_type_ehcache = "ehcache";
	
	/**
	 * 缓存类型redis
	 */
	public static final String cache_type_redis = "redis";
	
	/**
	 * URL缓存Key
	 */
	public static final String cache_name_ehcache_page = "SimplePageCachingFilter";
	
	/**
	 * 系统缓存，主要是权限和数据字典等
	 */
	public static final String cache_name_ehcache_system = "system";

	/**
	 * 系统缓存，主要是权限和数据字典等
	 */
	public static final String cache_name_redis_system = "system";

	public static final String CACHE_EHCACHE_FORM_TOKEN = "formToken";

    /**
     * 验证码缓存
     */
	public static final String CACHE_NAME_PIN_CODE = "PIN_CODE";

    /**
     *发送验证码时间及次数缓存
     */
	public static final String CACHE_NAME_PIN_CODE_TIMES = "PIN_CODE_TIMES";

	public static final String CACHE_NAME_WEA = "CACHE_NAME_WEA";

}
