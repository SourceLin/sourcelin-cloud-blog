package com.sourcelin.common.sensitive.config;

import java.io.IOException;
import java.util.Objects;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.sourcelin.common.sensitive.annotation.Sensitive;
import com.sourcelin.common.sensitive.enums.DesensitizedType;

/**
 * 数据脱敏序列化过滤
 *
 * @author sourcelin
 */
public class SensitiveJsonSerializer extends JsonSerializer<String> implements ContextualSerializer
{
    private DesensitizedType desensitizedType;
    private boolean enabled = true;

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException
    {
        if (enabled && desensitizedType != null)
        {
            gen.writeString(desensitizedType.desensitizer().apply(value));
        }
        else
        {
            gen.writeString(value);
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property)
            throws JsonMappingException
    {
        Sensitive annotation = property.getAnnotation(Sensitive.class);
        if (Objects.nonNull(annotation) && Objects.equals(String.class, property.getType().getRawClass()))
        {
            this.desensitizedType = annotation.desensitizedType();
            return this;
        }
        return prov.findValueSerializer(property.getType(), property);
    }
}
