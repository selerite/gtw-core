package io.gtw.platform.core.cache;
import com.fasterxml.jackson.core.type.TypeReference;
import io.gtw.platform.core.dao.dgraph.SchemaDao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import io.gtw.platform.core.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlobalPredicateCache {
    private static final Logger logger = LoggerFactory.getLogger(GlobalPredicateCache.class);
    public static HashMap<String, HashMap<String, Object>> PredicateCacheMap;

    public static void initCache() {
        PredicateCacheMap = new HashMap<>();

        // cache schema
        logger.debug("---------cache schema.----------------");
        SchemaDao schemaDao = new SchemaDao();
        String responseResult = schemaDao.querySchema();
        try {
            HashMap resultMap = JsonUtils.strToObject(responseResult, HashMap.class);
            ArrayList schemaList = (ArrayList)(((HashMap)resultMap.get("data")).get("schema"));
            schemaList.forEach(k -> PredicateCacheMap.put(((HashMap)k).get("predicate").toString(), (HashMap<String, Object>) k));
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.debug("---------schema cached ---------------");
    }

    public static Object schemaAt(String predicate) {
        return PredicateCacheMap.get(predicate);
    }
}
