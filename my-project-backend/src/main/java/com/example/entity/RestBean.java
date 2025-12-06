package com.example.entity;

import com.alibaba. fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import org.slf4j.MDC;

import java.util. Optional;

/**
 * 统一 REST 风格的响应实体类封装
 *
 * 设计目标：
 * 1. 统一前后端交互的数据格式
 * 2. 封装常见的响应场景（成功、失败、权限错误等）
 * 3. 支持请求追踪（通过 requestId）
 * 4. 类型安全（泛型支持）
 *
 * 使用 record 的好处：
 * - 自动生成构造器、getter、equals、hashCode、toString
 * - 不可变对象（线程安全）
 * - 代码简洁
 *
 * @param id      请求追踪 ID，用于日志关联和问题排查
 * @param code    HTTP 状态码（200 成功，400/401/403/500 等失败）
 * @param data    响应数据（泛型，可以是任何类型）
 * @param message 响应消息（成功提示或错误信息）
 * @param <T>     响应数据的类型
 */
public record RestBean<T>(long id, int code, T data, String message) {

    // ========== 成功响应 ==========

    /**
     * 成功响应（带数据）
     *
     * 使用场景：查询、创建、更新等操作成功后返回数据
     *
     * @param data 要返回的数据
     * @param <T>  数据类型
     * @return RestBean 实例
     *
     * 示例：
     * return RestBean.success(user);
     * → {"id":123456,"code":200,"data":{...},"message":"请求成功"}
     */
    public static <T> RestBean<T> success(T data) {
        return new RestBean<>(requestId(), 200, data, "请求成功");
    }

    /**
     * 成功响应（无数据）
     *
     * 使用场景：删除、更新等不需要返回数据的操作
     *
     * @param <T> 数据类型（虽然为 null，但保持泛型一致性）
     * @return RestBean 实例，data 为 null
     *
     * 示例：
     * return RestBean.success();
     * → {"id":123456,"code":200,"data":null,"message":"请求成功"}
     */
    public static <T> RestBean<T> success() {
        return success(null);
    }

    // ========== 失败响应（语义化方法）==========

    /**
     * 403 Forbidden - 禁止访问
     *
     * 使用场景：用户已登录，但没有权限访问该资源
     *
     * @param message 错误提示信息
     * @param <T>     数据类型
     * @return RestBean 实例
     *
     * 示例：
     * return RestBean.forbidden("您没有权限访问此资源");
     * → {"id":123456,"code":403,"data":null,"message":"您没有权限访问此资源"}
     */
    public static <T> RestBean<T> forbidden(String message) {
        return failure(403, message);
    }

    /**
     * 401 Unauthorized - 未授权
     *
     * 使用场景：用户未登录或 token 失效
     *
     * @param message 错误提示信息
     * @param <T>     数据类型
     * @return RestBean 实例
     *
     * 示例：
     * return RestBean.unauthorized("登录已过期，请重新登录");
     * → {"id":123456,"code":401,"data":null,"message":"登录已过期，请重新登录"}
     */
    public static <T> RestBean<T> unauthorized(String message) {
        return failure(401, message);
    }

    /**
     * 通用失败响应
     *
     * 使用场景：自定义状态码和错误信息
     *
     * @param code    HTTP 状态码
     * @param message 错误提示信息
     * @param <T>     数据类型
     * @return RestBean 实例
     *
     * 示例：
     * return RestBean.failure(400, "用户名格式不正确");
     * → {"id":123456,"code":400,"data":null,"message":"用户名格式不正确"}
     */
    public static <T> RestBean<T> failure(int code, String message) {
        return new RestBean<>(requestId(), code, null, message);
    }

    // ========== 工具方法 ==========

    /**
     * 将当前对象转换为 JSON 字符串
     *
     * 特点：
     * - 使用阿里的 fastjson2（性能优于 Jackson 和 Gson）
     * - WriteNulls 特性：即使字段为 null 也会输出（前端可以统一处理）
     *
     * 使用场景：
     * - 手动构造 HTTP 响应体
     * - 日志记录
     * - 消息队列传输
     *
     * @return JSON 字符串
     *
     * 示例：
     * RestBean.success(user).asJsonString();
     * → "{"id":123456,"code": 200,"data":{"username":"zhangsan"},"message":"请求成功"}"
     */
    public String asJsonString() {
        return JSONObject.toJSONString(this, JSONWriter.Feature.WriteNulls);
    }

    /**
     * 获取当前请求的追踪 ID
     *
     * 核心机制：从 MDC（Mapped Diagnostic Context）中获取
     *
     * MDC 是什么？
     * - SLF4J 提供的线程上下文存储机制
     * - 每个线程有独立的 Map，用于存储请求级别的上下文信息
     * - 常用于分布式系统的链路追踪
     *
     * 工作流程：
     * 1. 请求进入时，过滤器/拦截器生成唯一 ID 并放入 MDC
     * 2. 整个请求处理过程中，所有日志和响应都可以获取到这个 ID
     * 3. 请求结束时，清除 MDC 避免内存泄漏
     *
     * @return 请求追踪 ID，如果 MDC 中没有则返回 0
     *
     * 示例 MDC 设置（通常在过滤器中）：
     * MDC.put("reqId", String.valueOf(System.currentTimeMillis()));
     */
    private static long requestId() {
        // Optional 优雅处理 null：如果 MDC 中没有 "reqId"，使用 "0"
        String requestId = Optional.ofNullable(MDC.get("reqId")).orElse("0");
        return Long.parseLong(requestId);
    }
}