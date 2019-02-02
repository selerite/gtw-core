package io.gtw.platform.core.utils;

public class EnvUtils {
    public static String DgraphHostnameFromEnv = System.getenv("CORE_DGRAPH_HOSTNAME");
    public static String DgraphPortFromEnv = System.getenv("CORE_DGRAPH_PORT");
    public static String DgraphHttpPortFromEnv = System.getenv("CORE_DGRAPH_HTTP_PORT");
}
