package com.example.filter;

import com. example.utils.Const;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet. http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet. http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 跨域资源共享（CORS）过滤器
 *
 * 作用：
 * 1. 解决浏览器的同源策略限制，允许前端跨域访问后端 API
 * 2. 统一配置跨域规则，支持从配置文件灵活调整
 * 3. 在请求处理链的最前面执行，确保所有响应都包含 CORS 头
 *
 * 应用场景：
 * - 前后端分离项目（前端 localhost:5173，后端 localhost:8081）
 * - 微服务架构中的服务间通信
 * - 开发环境和生产环境的跨域配置分离
 *
 * CORS 工作流程：
 * 1. 浏览器发起跨域请求前，先发送 OPTIONS 预检请求
 * 2. 服务器返回 CORS 响应头，告诉浏览器是否允许跨域
 * 3. 浏览器根据响应头决定是否发送实际请求
 */
@Component  // 注册为 Spring Bean
@Order(Const.ORDER_CORS)  // 控制过滤器执行顺序（数字越小越先执行）
// CORS 过滤器需要在认证过滤器之前执行
public class CorsFilter extends HttpFilter {

    // ========== 从配置文件注入 CORS 配置 ==========

    /**
     * 允许跨域的源站点
     *
     * 配置示例（application.yml）：
     * spring:
     *   web:
     *     cors:
     *       origin: "*"                          # 允许所有来源（开发环境）
     *       origin:  "http://localhost:5173"     # 只允许前端开发服务器（生产推荐）
     *       origin:  "https://www.example.com"   # 只允许生产域名
     */
    @Value("${spring.web.cors.origin}")
    String origin;

    /**
     * 是否允许携带凭证（Cookie、Authorization header）
     *
     * 配置示例：
     * spring:
     *   web:
     *     cors:
     *       credentials: true   # 允许携带 Cookie 和 JWT token
     *       credentials: false  # 不允许（更安全，但前端无法携带认证信息）
     *
     * 注意：
     * - 如果设置为 true，origin 不能是 "*"，必须指定具体域名
     * - 前端请求时需要设置 withCredentials: true（axios）
     */
    @Value("${spring.web.cors.credentials}")
    boolean credentials;

    /**
     * 允许的 HTTP 方法
     *
     * 配置示例：
     * spring:
     *   web:
     *     cors:
     *       methods:  "*"                        # 允许所有方法
     *       methods: "GET, POST, PUT, DELETE"   # 只允许指定方法
     */
    @Value("${spring.web.cors.methods}")
    String methods;

    // ========== 核心过滤逻辑 ==========

    /**
     * 过滤器的核心方法
     *
     * 执行流程：
     * 1. 请求到达 → 添加 CORS 响应头
     * 2. 继续执行过滤器链（认证、业务逻辑等）
     * 3. 响应返回给浏览器（已包含 CORS 头）
     *
     * @param request  HTTP 请求对象
     * @param response HTTP 响应对象
     * @param chain    过滤器链（用于传递给下一个过滤器）
     */
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // 1. 添加 CORS 响应头（在业务处理之前）
        this. addCorsHeader(request, response);

        // 2. 继续执行后续过滤器和业务逻辑
        //    如果这是 OPTIONS 预检请求，后续过滤器可以直接返回 200
        chain.doFilter(request, response);
    }

    // ========== 私有辅助方法 ==========

    /**
     * 添加所有必需的 CORS 响应头
     *
     * CORS 响应头说明：
     * - Access-Control-Allow-Origin: 允许哪些源访问
     * - Access-Control-Allow-Methods: 允许哪些 HTTP 方法
     * - Access-Control-Allow-Headers:  允许哪些请求头
     * - Access-Control-Allow-Credentials: 是否允许携带凭证
     *
     * @param request  HTTP 请求（用于动态解析 Origin）
     * @param response HTTP 响应（添加响应头）
     */
    private void addCorsHeader(HttpServletRequest request, HttpServletResponse response) {
        // 1. 设置允许的源站点（支持通配符或具体域名）
        response.addHeader("Access-Control-Allow-Origin", this.resolveOrigin(request));

        // 2. 设置允许的 HTTP 方法
        response.addHeader("Access-Control-Allow-Methods", this.resolveMethod());

        // 3. 设置允许的请求头
        //    Authorization: JWT token 等认证信息
        //    Content-Type: JSON 请求体类型
        response.addHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");

        // 4. 如果配置允许携带凭证，添加对应响应头
        if (credentials) {
            response.addHeader("Access-Control-Allow-Credentials", "true");
        }
    }

    /**
     * 解析配置文件中的请求方法
     *
     * 逻辑：
     * - 如果配置为 "*"，返回所有常用 HTTP 方法
     * - 否则返回配置的具体方法列表
     *
     * @return 完整的方法列表字符串
     *
     * 示例：
     * 配置 methods: "*"
     * → 返回 "GET, HEAD, POST, PUT, DELETE, OPTIONS, TRACE, PATCH"
     *
     * 配置 methods: "GET, POST"
     * → 返回 "GET, POST"
     */
    private String resolveMethod() {
        return methods.equals("*")
                ? "GET, HEAD, POST, PUT, DELETE, OPTIONS, TRACE, PATCH"
                : methods;
    }

    /**
     * 解析配置文件中的请求原始站点
     *
     * 逻辑：
     * - 如果配置为 "*"，动态返回请求头中的 Origin（实现真正的通配符）
     * - 否则返回配置的具体域名（更安全）
     *
     * 为什么要这样处理？
     * - 浏览器要求：当 credentials=true 时，Access-Control-Allow-Origin 不能是 "*"
     * - 解决方案：读取请求头的 Origin，动态返回给浏览器
     *
     * @param request HTTP 请求对象
     * @return 解析得到的 Origin 值
     *
     * 示例：
     * 配置 origin: "*"，请求来自 http://localhost:5173
     * → 返回 "http://localhost:5173"
     *
     * 配置 origin: "https://www.example.com"
     * → 返回 "https://www.example.com"（忽略实际请求来源）
     */
    private String resolveOrigin(HttpServletRequest request) {
        return origin.equals("*")
                ? request.getHeader("Origin")  // 动态获取请求来源
                : origin;                       // 使用配置的固定值
    }
}