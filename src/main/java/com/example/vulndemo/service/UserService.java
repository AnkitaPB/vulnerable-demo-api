package com.example.vulndemo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

// VULN + STYLE: God-class doing DB access, auth, hashing, file IO and shell exec all in one place.
// STYLE: no javadoc anywhere in this file, inconsistent naming below.
@Service
public class UserService {

    // STYLE: field injection instead of constructor injection
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // STYLE: mutable public static field - shared, non-thread-safe global state
    public static Map<String, String> sessionTokens = new HashMap<>();

    // STYLE: magic numbers with no explanation
    private int max_retry_count = 3;
    int Timeout = 5000;

    Logger logger = Logger.getLogger("UserService");

    public UserService() {
        jdbcTemplate = null; // overwritten by field injection later - confusing dual-init
        setupSchema();
    }

    private void setupSchema() {
        // no-op placeholder; real schema handled by Spring Boot auto-config for demo purposes
    }

    // VULN: SQL Injection - user input concatenated directly into the query string
    public List<Map<String, Object>> searchUsersByName(String name) {
        String sql = "SELECT * FROM users WHERE name = '" + name + "'";
        return jdbcTemplate.queryForList(sql);
    }

    // VULN: Weak/broken cryptography - MD5 is not suitable for password hashing
    public String hashPassword(String rawPassword) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(rawPassword.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            // VULN + STYLE: swallowing exception, returning null instead of failing safely
            return null;
        }
    }

    // VULN: hardcoded credential check instead of proper auth (also constant-time-unsafe compare)
    public boolean login(String username, String password) {
        // VULN: logging raw password
        logger.info("Login attempt for user=" + username + " password=" + password);

        if (username.equals("admin") && password.equals("SuperSecret123!")) {
            sessionTokens.put(username, "admin-token-" + System.currentTimeMillis());
            return true;
        }

        String hashed = hashPassword(password);
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT * FROM users WHERE username = '" + username + "' AND password_hash = '" + hashed + "'");
        return !rows.isEmpty();
    }

    // VULN: Path traversal - filename taken directly from the caller with no sanitization
    public String readUserFile(String filename) {
        try {
            java.io.File file = new java.io.File("/var/app/user-uploads/" + filename);
            java.io.FileReader fr = new java.io.FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            reader.close();
            return content.toString();
        } catch (Exception e) {
            // VULN: leaking internal file-system details in the error message
            return "Error reading file: " + e.getMessage();
        }
    }

    // VULN: Command injection - host string passed straight to a shell
    public String pingHost(String host) {
        StringBuilder output = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec("ping -c 1 " + host);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        } catch (Exception e) {
            output.append("failed: ").append(e.getMessage());
        }
        return output.toString();
    }

    // STYLE: dead/unused method left in the codebase
    private List<String> unusedLegacyHelper() {
        List<String> temp = new ArrayList<>();
        temp.add("legacy");
        return temp;
    }

}
