package com.kronos.plugin.base;

import java.io.IOException;

public interface AsmHelper {
    byte[] modifyClass(byte[] srcClass) throws IOException;
}
