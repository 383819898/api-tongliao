package com.chinagpay.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * controller中处理post(get)提交参数到第三方url
 *
 * @author dong.gang
 * @date 2018年2月4日
 */
public class Redirect {

    private static final Logger LOG = LoggerFactory.getLogger(Redirect.class);

    /**
     * 模拟Form提交
     *
     * @param url
     *            提交地址
     * @param target
     *            window target,set null if current window
     * @param charset
     *            html charset
     * @param paramMap
     *            提交参数域
     * @param reqMethod
     *            请求方式 get or post
     * @param response
     *            HttpServletResponse
     * @throws IOException
     *             处理失败抛出异常
     */
    public static void sendRedirect(String url, String target, String charset, Map<String, String> paramMap,
        String reqMethod, HttpServletResponse response) throws IOException {
        if (url == null) {
            throw new IOException("can't find redirect url.");
        }
        String html = toHTML(url, target, paramMap, charset, reqMethod);
        LOG.debug("Redirect:\r\n" + html);
        response.setContentType("text/html; charset=" + charset);
        response.getWriter().write(html);
    }

    /**
     * 模拟Form提交
     *
     * @param url
     *            提交地址
     * @param target
     *            window target,set null if current window
     * @param charset
     *            html charset
     * @param paramMap
     *            提交参数域
     * @param response
     *            HttpServletResponse
     * @throws IOException
     *             处理失败抛出异常
     */
    public static void sendRedirect(String url, String target, String charset, Map<String, String> paramMap,
        HttpServletResponse response) throws IOException {
        if (url == null) {
            throw new IOException("can't find redirect url.");
        }
        String html = toHTML(url, target, paramMap, charset, "post");
        LOG.debug("Redirect:\r\n" + html);
        response.setContentType("text/html; charset=" + charset);
        response.getWriter().write(html);
    }

    /**
     * RequestDispatcher.forward提交
     *
     * @param path
     *            提交ServletPath
     * @param attrMap
     *            属性列表
     * @param request
     *            HttpServletRequest
     * @param response
     *            HttpServletResponse
     * @throws IOException
     *             处理失败抛出异常
     * @throws ServletException
     *             处理失败抛出异常
     */
    public static void forward(String path, Map<String, Object> attrMap, HttpServletRequest request,
        HttpServletResponse response) throws IOException, ServletException {
        if (path == null) {
            throw new IOException("can't find forward path.");
        }
        StringBuffer buf = new StringBuffer("Forward:URL=[");
        buf.append(path).append("];");
        for (String key : attrMap.keySet()) {
            Object value = attrMap.get(key);
            request.setAttribute(key, value);
            buf.append(key).append("=[").append(value).append("];");
        }
        LOG.debug(buf.toString());
        RequestDispatcher rd = request.getRequestDispatcher(path);
        if (rd != null) {
            rd.forward(request, response);
        } else {
            throw new ServletException("Can not find [" + path + "];RequestDispatcher is null!");
        }
    }

    private static String toHiddenTag(String name, String value) {
        return new StringBuffer("<input type=\"hidden\" name=\"").append(name).append("\" value=\"").append(value)
            .append("\" />").toString();
    }

    /**
     * 产生HTML代码
     *
     * @param url
     *            提交地址
     * @param target
     *            window target,set null if current window
     * @param charset
     *            html charset
     * @param paramMap
     *            提交参数域
     * @return HTML代码
     */
    public static String toHTML(String url, String target, Map<String, String> paramMap, String charset,
        String reqMethod) {
        StringBuffer html = new StringBuffer();
        html.append("<html>");
        html.append("<head>");
        html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=").append(charset).append("\"/>");
        html.append("<title> </title>");
        html.append("</head>");
        html.append("<body>");
        html.append("<form name=\"AutoForm\"");
        if (target != null) {
            html.append(" target=\"").append(target).append("\"");
        }
        html.append(" action=\"").append(url).append("\" method=\"").append(reqMethod).append("\">");
        for (String key : paramMap.keySet()) {
            html.append(toHiddenTag(key, paramMap.get(key)));
        }
        html.append("</form>");
        html.append("<script type=\"text/javascript\">");
        html.append("document.AutoForm.submit();");
        html.append("</script>");
        html.append("</body></html>");
        return html.toString();
    }
}
