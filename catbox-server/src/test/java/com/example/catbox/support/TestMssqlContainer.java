package com.example.catbox.support;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Deprecated: Tests now declare their own Testcontainers. This extension is a no-op.
 */
@Deprecated
public class TestMssqlContainer implements BeforeAllCallback, AfterAllCallback {
    @Override
    public void beforeAll(ExtensionContext context) {
        // no-op
    }

    @Override
    public void afterAll(ExtensionContext context) {
        // no-op
    }
}
