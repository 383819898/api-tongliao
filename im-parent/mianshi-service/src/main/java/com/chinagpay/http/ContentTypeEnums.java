package com.chinagpay.http;

/**
 * http contentType mime 和 charset枚举
 *
 * @Author:Gang.Dong
 * @Date: Created in 14:12 2019/3/4.
 */
public enum ContentTypeEnums {

    MIME_TYPE_APPLICATION_ATOM_XML("application/atom+xml"),

    MIME_TYPE_APPLICATION_FORM_URLENCODED("application/x-www-form-urlencoded"),

    MIME_TYPE_APPLICATION_JSON("application/json"),

    MIME_TYPE_APPLICATION_OCTET_STREAM("application/octet-stream"),

    MIME_TYPE_APPLICATION_SVG_XML("application/svg+xml"),

    MIME_TYPE_APPLICATION_XHTML_XML("application/xhtml+xml"),

    MIME_TYPE_APPLICATION_XML("application/xml"),

    MIME_TYPE_MULTIPART_FORM_DATA("multipart/form-data"),

    MIME_TYPE_TEXT_HTML("text/html"),

    MIME_TYPE_TEXT_PLAIN("text/plain"),

    MIME_TYPE_TEXT_XML("text/xml"),

    CHAR_SET_UTF_8("UTF-8"),

    CHAR_SET_ISO_8859_1("ISO-8859-1"),

    CHAR_SET_GBK("GBK");

    // 枚举值
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    ContentTypeEnums(String code) {
        this.code = code;
    }
}
