package com.saptarshidebnath.processrunner.lib.utilities;

import java.io.File;
import java.io.IOException;

public class Utilities {

    public static File createTempLogDump() throws IOException {
        return File.createTempFile(Constants.FILE_PREFIX_NAME_LOG_DUMP, Constants.FILE_SUFFIX_JSON);
    }
}
