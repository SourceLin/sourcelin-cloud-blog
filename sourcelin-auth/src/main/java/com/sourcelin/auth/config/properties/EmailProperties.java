package com.sourcelin.auth.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sourcelin.email")
public class EmailProperties {
    
    private String host;
    private Integer port;
    private String username;
    private String password;
    private String from;
    private Boolean useSsl = true;
    
    public String getHost() {
        return host;
    }
    
    public void setHost(String host) {
        this.host = host;
    }
    
    public Integer getPort() {
        return port;
    }
    
    public void setPort(Integer port) {
        this.port = port;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getFrom() {
        return from;
    }
    
    public void setFrom(String from) {
        this.from = from;
    }
    
    public Boolean getUseSsl() {
        return useSsl;
    }
    
    public void setUseSsl(Boolean useSsl) {
        this.useSsl = useSsl;
    }
}