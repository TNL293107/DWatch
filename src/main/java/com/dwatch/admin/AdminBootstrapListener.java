package com.dwatch.admin;

import com.dwatch.common.AppConfig;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * On startup, creates the configured admin account (ADMIN_BOOTSTRAP_USER /
 * ADMIN_BOOTSTRAP_PASSWORD from .env) if it doesn't exist yet, with a bcrypt
 * hash. The default admin/admin123 seed row from database.sql keeps working
 * on its own via AdminDAO's legacy-plaintext fallback + lazy rehash, so this
 * is purely opt-in for operators who want a different bootstrap account.
 */
@WebListener
public class AdminBootstrapListener implements ServletContextListener {

    private final AdminDAO adminDAO = new AdminDAO();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String username = AppConfig.get("ADMIN_BOOTSTRAP_USER", "");
        String password = AppConfig.get("ADMIN_BOOTSTRAP_PASSWORD", "");
        if (username.isEmpty() || password.isEmpty()) return;
        if (!adminDAO.exists(username)) {
            adminDAO.insert(username, password);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {}
}
