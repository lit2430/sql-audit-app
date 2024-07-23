package com.someway.utils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

/**
 * @Author lit
 * @Date 2024-07-06 17:54
 **/
@Slf4j
public class CustFreemarkerUtils {


    public static final String DEFAULT_VERSION = "2.3.31";


    public static String replace(String templateName, String templateText, Map<String, Object> params) {
        Configuration configuration = new Configuration(new Version(DEFAULT_VERSION));
        Writer writer = new StringWriter();
        try {
            Template template = new Template(templateName, new StringReader(templateText), configuration);
            template.process(params, writer);
        } catch (IOException | TemplateException e) {
            throw new RuntimeException(e);
        }
        log.info("The result of template replacement is :{}", writer.toString());
        return writer.toString();

    }


}
