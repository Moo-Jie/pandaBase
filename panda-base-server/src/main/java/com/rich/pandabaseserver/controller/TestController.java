package com.rich.pandabaseserver.controller;

import com.rich.pandabaseserver.common.response.BaseResponse;
import com.rich.pandabaseserver.common.ResultUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器 - 用于调试 Session 和 Cookie
 *
 * @author DuRuiChi
 * @create 2025/12/11
 **/
@RestController
@RequestMapping("/test")
public class TestController {

    /**
     * 测试设置 Session
     */
    @GetMapping("/set-session")
    public BaseResponse<Map<String, Object>> setSession(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        session.setAttribute("test_key", "test_value");
        session.setAttribute("timestamp", System.currentTimeMillis());
        
        Map<String, Object> result = new HashMap<>();
        result.put("sessionId", session.getId());
        result.put("isNew", session.isNew());
        result.put("creationTime", session.getCreationTime());
        result.put("maxInactiveInterval", session.getMaxInactiveInterval());
        result.put("message", "Session 已设置");
        
        return ResultUtils.success(result);
    }

    /**
     * 测试读取 Session
     */
    @GetMapping("/get-session")
    public BaseResponse<Map<String, Object>> getSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        
        Map<String, Object> result = new HashMap<>();
        if (session == null) {
            result.put("hasSession", false);
            result.put("message", "没有找到 Session");
        } else {
            result.put("hasSession", true);
            result.put("sessionId", session.getId());
            result.put("testKey", session.getAttribute("test_key"));
            result.put("timestamp", session.getAttribute("timestamp"));
            result.put("message", "Session 存在");
        }
        
        return ResultUtils.success(result);
    }

    /**
     * 查看请求头信息
     */
    @GetMapping("/headers")
    public BaseResponse<Map<String, Object>> getHeaders(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Map<String, String> headers = new HashMap<>();
        
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }
        
        result.put("headers", headers);
        result.put("hasCookie", request.getHeader("Cookie") != null);
        result.put("cookieValue", request.getHeader("Cookie"));
        result.put("requestURL", request.getRequestURL().toString());
        
        return ResultUtils.success(result);
    }

    /**
     * 综合测试：一次性检查所有信息
     */
    @GetMapping("/debug-info")
    public BaseResponse<Map<String, Object>> getDebugInfo(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        
        // 1. 请求头信息
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }
        result.put("headers", headers);
        
        // 2. Cookie 信息
        result.put("hasCookie", request.getHeader("Cookie") != null);
        result.put("cookieValue", request.getHeader("Cookie"));
        
        // 3. Session 信息
        HttpSession session = request.getSession(false);
        Map<String, Object> sessionInfo = new HashMap<>();
        if (session != null) {
            sessionInfo.put("exists", true);
            sessionInfo.put("sessionId", session.getId());
            sessionInfo.put("isNew", session.isNew());
            sessionInfo.put("creationTime", session.getCreationTime());
            sessionInfo.put("lastAccessedTime", session.getLastAccessedTime());
            sessionInfo.put("maxInactiveInterval", session.getMaxInactiveInterval());
            
            // Session 中的所有属性
            Map<String, Object> attributes = new HashMap<>();
            Enumeration<String> attrNames = session.getAttributeNames();
            while (attrNames.hasMoreElements()) {
                String attrName = attrNames.nextElement();
                Object attrValue = session.getAttribute(attrName);
                attributes.put(attrName, attrValue != null ? attrValue.toString() : null);
            }
            sessionInfo.put("attributes", attributes);
        } else {
            sessionInfo.put("exists", false);
            sessionInfo.put("message", "没有找到 Session（未登录或 Cookie 未传递）");
        }
        result.put("session", sessionInfo);
        
        // 4. 请求信息
        Map<String, String> requestInfo = new HashMap<>();
        requestInfo.put("method", request.getMethod());
        requestInfo.put("requestURI", request.getRequestURI());
        requestInfo.put("requestURL", request.getRequestURL().toString());
        requestInfo.put("contextPath", request.getContextPath());
        requestInfo.put("servletPath", request.getServletPath());
        result.put("request", requestInfo);
        
        return ResultUtils.success(result);
    }
}
