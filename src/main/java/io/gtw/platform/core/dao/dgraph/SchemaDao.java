package io.gtw.platform.core.dao.dgraph;

import io.gtw.platform.core.utils.EnvUtils;
import io.gtw.platform.core.utils.HttpUtils;
import io.gtw.platform.core.utils.RequestDataItem;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class SchemaDao {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public String querySchema() {
        CloseableHttpClient closeableHttpClient = HttpUtils.createHttpClient();
        String dgraphHostname = EnvUtils.DgraphHostnameFromEnv;
        String dgraphHttpPort = EnvUtils.DgraphHttpPortFromEnv;
        String dgraphUrl = "http://" + dgraphHostname + ":" + dgraphHttpPort + "/query";
        return HttpUtils.doPost(closeableHttpClient, new RequestDataItem(dgraphUrl), null, "schema {}", "UTF-8");
    }
}
