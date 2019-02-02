package io.gtw.platform.core.dao.dgraph;

import io.dgraph.DgraphClient;
import io.dgraph.DgraphProto.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class ItemDao {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public String queryItemByXid(String xid, String lang) {
        DgraphClient dgraphClient = DgraphUtils.createDgraphClient();
        String query =
                "query all($xid: string){\n" +
                "  all(func: eq(xid, $xid)) {\n" +
                "     <http://schema.org/name>" + lang + "\n" +
                "  }\n" +
                "}\n";
        Map<String, String> vars = new HashMap<>();
        vars.put("$xid", xid);
        Response response = dgraphClient.newTransaction().queryWithVars(query, vars);
        String jsonString = response.getJson().toStringUtf8();
        logger.debug(jsonString);
        return jsonString;
    }
}
