package io.gtw.platform.core.service.impl;

import io.gtw.platform.core.dao.dgraph.ItemDao;
import io.gtw.platform.core.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemDao itemDao;

    @Autowired
    public ItemServiceImpl(ItemDao itemDao) {
        this.itemDao = itemDao;
    }

    @Override
    public String getItemByXid(String xid, String lang) {
        return itemDao.queryItemByXid(xid, lang);
    }
}
