package com.example.vulndemo.controller;

import com.example.vulndemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// VULN: wide-open CORS - any website can call these endpoints with credentials
@CrossOrigin(origins = "*", allowCredentials = "true")
@RestController
@RequestMapping("/api")
public class DemoController {

    @Autowired
    UserService userService; // STYLE: field injection, package-private, no access modifier convention

    // VULN: no authentication/authorization guard on an admin-style endpoint
    @GetMapping("/admin/sessions")
    public Map<String, String> getAllSessions() {
        return UserService.sessionTokens;
    }

    // VULN: SQL injection reachable directly from an unauthenticated GET endpoint
    @GetMapping("/users/search")
    public List<Map<String, Object>> search(@RequestParam String name) {
        return userService.searchUsersByName(name);
    }

    // VULN: no input validation, credentials logged, weak comparison
    @PostMapping("/login")
    public Map<String, Object> login(@RequestParam String username, @RequestParam String password) {
        boolean ok = userService.login(username, password);
        Map<String, Object> result = new HashMap<>();
        result.put("success", ok);
        if (ok) {
            // VULN: leaking the internal session token map contents back to the client
            result.put("sessions", UserService.sessionTokens);
        }
        return result;
    }

    // VULN: path traversal - filename passed straight through with no sanitization/allowlist
    @GetMapping("/files")
    public String getFile(@RequestParam String filename) {
        return userService.readUserFile(filename);
    }

    // VULN: command injection - host forwarded straight into a shell command
    @GetMapping("/ping")
    public String ping(@RequestParam String host) {
        return userService.pingHost(host);
    }

    // VULN: exposes the raw exception/stack trace to the client (info disclosure)
    @GetMapping("/crash")
    public String crash() {
        try {
            int x = 1 / 0;
            return "unreachable " + x;
        } catch (Exception e) {
            return "Internal error: " + e.toString() + " | " + java.util.Arrays.toString(e.getStackTrace());
        }
    }

    // STYLE: unused parameter, inconsistent naming (mixed camelCase/snake_case), no return-type documentation
    @GetMapping("/legacyPing")
    public String legacy_ping(@RequestParam(required = false) String unused_param) {
        return "pong";
    }

}
