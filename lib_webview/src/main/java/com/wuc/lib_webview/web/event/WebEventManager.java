package com.wuc.lib_webview.web.event;

import java.util.HashMap;

/**
 * @author: wuchao
 * @date: 2019/2/1 15:24
 * @desciption: js 互调管理类
 */
public class WebEventManager {

    private static final HashMap<String, BaseWebEvent> EVENTS = new HashMap<>();

    private WebEventManager() {
    }

    public static WebEventManager getInstance() {
        return Holder.INSTANCE;
    }

    public WebEventManager addEvent(String name, BaseWebEvent event) {
        EVENTS.put(name, event);
        return this;
    }

    public BaseWebEvent createEvent(String action) {
        BaseWebEvent event = EVENTS.get(action);
        if (event == null) {
            event = new UndefineEvent();
        }
        return event;
    }

    private static class Holder {
        private static final WebEventManager INSTANCE = new WebEventManager();
    }
}
