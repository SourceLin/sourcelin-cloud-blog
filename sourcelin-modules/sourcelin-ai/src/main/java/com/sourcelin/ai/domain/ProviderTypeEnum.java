package com.sourcelin.ai.domain;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AI 提供商类型枚举
 * 后续新增 Provider 只需在此加一行枚举值
 */
public enum ProviderTypeEnum {

    DIRECT_LLM("direct_llm", "直连大模型"),
    DIRECT_IMAGE("direct_image", "直连图像模型"),
    DIFY("dify", "Dify 编排平台"),
    FASTGPT("fastgpt", "FastGPT 知识库"),
    ;

    private final String code;
    private final String label;

    ProviderTypeEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    /**
     * 根据 code 查找枚举，找不到时抛异常
     */
    public static ProviderTypeEnum fromCode(String code) {
        for (ProviderTypeEnum type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unsupported provider type: " + code);
    }

    /**
     * 供前端下拉框使用的选项列表
     */
    public static List<com.sourcelin.ai.api.domain.AiProviderOptionVO> toOptions() {
        return Arrays.stream(values())
                .filter(type -> type != FASTGPT)
                .map(t -> new com.sourcelin.ai.api.domain.AiProviderOptionVO(t.code, t.label))
                .collect(Collectors.toList());
    }
}
