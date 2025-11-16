package com.Spring.MindScribe.utils;

import java.util.UUID;

public final class SlugUtil {
    private SlugUtil() {}
    public static String randomSlug() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }
}