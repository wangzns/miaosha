package com.sky.miaosha.config;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

/**
 * KeepAliveTimeOut: 多少毫秒后不响应的断开keepAlive
 * maxKeepAliveRequests: 多少次请求后keepAlive断开失效
 *
 *
 * 定制化tomcat配置
 * @author : wang zns
 * @date : 2020-07-29
 */
@Configuration
public class CustomTomcatConfig implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

    @Override
    public void customize(ConfigurableWebServerFactory factory) {
            // 使用对应的工厂类接口实现定制化配置
        TomcatServletWebServerFactory tomcatServletWebServerFactory = (TomcatServletWebServerFactory) factory;
        tomcatServletWebServerFactory.addConnectorCustomizers(new TomcatConnectorCustomizer() {
            @Override
            public void customize(Connector connector) {
                Http11NioProtocol protocolHandler = (Http11NioProtocol) connector.getProtocolHandler();
                // 设置http长连接超时时间为30秒
                protocolHandler.setKeepAliveTimeout(30000);
                // 设置http长连接的最大请求数，超过该请求数则断开长连接
                protocolHandler.setMaxKeepAliveRequests(10000);
            }
        });
    }
}
