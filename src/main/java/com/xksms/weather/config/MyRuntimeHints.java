package com.xksms.weather.config;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.lang.Nullable;

public class MyRuntimeHints implements RuntimeHintsRegistrar {
    @Override
    public void registerHints(RuntimeHints hints, @Nullable ClassLoader classLoader) {
        // 注册 file 目录下的所有 csv 文件
        hints.resources().registerPattern("file/*.csv");
        // 或者指定精确路径：hints.resources().registerPattern("file/china-city-list.csv");
    }
}