package org.feosd.admin.system.web;

import org.feosd.admin.system.web.api.*;
import org.glassfish.jersey.server.ResourceConfig;


public class SystemResourceConfig {
    public static void config(ResourceConfig config) {
        config.register(SysAnnouncementApi.class);
        config.register(SysUserApi.class);
        config.register(SysDepartApi.class);
    }
}
