/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 * 
 */

/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package au.com.tyo;

public class Logger {

    private static final int LEVEL_DEBUG = 1;
    private static final int LEVEL_INFO = 2;
    private static final int LEVEL_WARN = 4;
    private static final int LEVEL_VERBOSE = 8;
    private static final int LEVEL_ERROR = 16;
    public static final int LEVEL_ALL = LEVEL_DEBUG | LEVEL_INFO | LEVEL_WARN | LEVEL_VERBOSE | LEVEL_ERROR;

    private static Logger instance;

    private LogListener logListener;

    private static int level;

    public static Logger getInstance() {
        if (null != instance)
            instance = new Logger();
        return instance;
    }

    public interface LogListener {
        void onError(Throwable e);
        void onError(Throwable e, String messag);
        void onError();

        void onDebug(Throwable e);
        void onDebug(Throwable e, String messag);
        void onDebug();

        void onVerbose(Throwable e);
        void onVerbose(Throwable e, String messag);
        void onVerbose();

        void onInfo(Throwable e);
        void onInfo(Throwable e, String messag);
        void onInfo();

        void onWarning(Throwable e);
        void onWarning(Throwable e, String messag);
        void onWarning();
    }

    public LogListener getLogListener() {
        return logListener;
    }

    public void setLogListener(LogListener logListener) {
        this.logListener = logListener;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public static void error(String tag, Throwable ex, String messgae) {

    }

    public static void debug(String tag, String messgae) {

    }

    public static void info(String tag, String messgae) {

    }

    public static void verbose(String tag, String messgae) {

    }

    public static void warn(String tag, String messgae) {

    }
}
