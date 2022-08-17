package com.shiku.mianshi.utils.sendpay.sdk;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shiku.mianshi.utils.sendpay.sdk.encrypt.CryptoUtil;
import com.shiku.mianshi.utils.sendpay.sdk.util.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付下单报文签名（仅供参考）
 *
 * @author sandpay
 */
public class FastPayServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(FastPayServlet.class);

    private static final long serialVersionUID = 1L;

    /**
     * 加载公私钥并初始化（仅需一次）
     */
    @Override
    public void init() throws ServletException {
        super.init();
        try {
            ConfigurationManager.loadProperties(new String[]{"sandPayConfig"});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 签约报文签名
     *
     * @param req
     * @param resp
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {

        Map<String, String[]> params = req.getParameterMap();
        Map<String, String> map = new HashMap<String, String>();
        for (String key : params.keySet()) {
            String[] values = params.get(key);
            if (values.length > 0) {
                map.put(key, values[0]);
            }
        }

     // 组后台报文
        JSONObject head = new JSONObject();
        head.put("version", map.get("version"));
        head.put("method", map.get("method"));
        head.put("productId", map.get("productId"));
        head.put("accessType", map.get("accessType"));
        head.put("mid", map.get("mid"));
        head.put("plMid", map.get("plMid"));
        head.put("channelType", map.get("channelType"));
        head.put("reqTime", map.get("reqTime"));

        JSONObject body = new JSONObject();
        body.put("userId", map.get("userId"));
        body.put("clearCycle", map.get("clearCycle"));
        body.put("currencyCode", map.get("currencyCode"));
        body.put("frontUrl", map.get("frontUrl"));
        body.put("notifyUrl", map.get("notifyUrl"));
        body.put("orderCode", map.get("orderCode"));
        body.put("orderTime", map.get("orderTime"));
        body.put("totalAmount", map.get("totalAmount"));
        body.put("body", map.get("body"));
        body.put("subject", map.get("subject"));
        body.put("extend", map.get("extend"));

        JSONObject data = new JSONObject();
        data.put("head", head);
        data.put("body", body);

        try {
            // 报文签名
            String reqSign = CryptoUtil.digitalSign(data.toJSONString());

            JSONObject respJson = new JSONObject();
            respJson.put("data", JSON.toJSONString(data));
            respJson.put("sign", reqSign);
            resp.getOutputStream().write(respJson.toJSONString().getBytes("UTF-8"));
        } catch (Exception e) {
            logger.error("CashierPayServlet post error <<<", e);
        }
    }

}
