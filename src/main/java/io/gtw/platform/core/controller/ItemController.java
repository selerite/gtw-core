package io.gtw.platform.core.controller;

import io.gtw.platform.core.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/core/v1/items")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @RequestMapping(value = "", method = {RequestMethod.GET})
    public @ResponseBody String getItemByXid(
            @RequestParam(value = "xid") String itemId,
            @RequestParam(value = "lang", required = false, defaultValue = "@zh") String lang) {
        return itemService.getItemByXid(itemId, lang);
    }
}
