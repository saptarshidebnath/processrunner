package name.saptarshidebnath.processrunner.lib.utilities;

import java.io.File;

/**
 * Created by saptarshi on 11/17/2016.
 */
public class ProcessRunnerFactory {
    public static ProcessRunner getProcessRunner(){
        return new ProcessRunner() {
            public int run() {
                return 0;
            }

            public File getSout() {
                return null;
            }

            public File getSysOut() {
                return null;
            }

            public File getOutPut() {
                return null;
            }
        }
    }
}
