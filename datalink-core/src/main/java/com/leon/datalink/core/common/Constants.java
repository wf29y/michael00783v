

package com.leon.datalink.core.common;

import com.leon.datalink.core.utils.StringUtils;

import java.nio.file.Paths;

/**
 * Constants.
 *
 * @author Leon
 */
public class Constants {

    public static final String NULL = "";

    public static final String ACCESS_TOKEN = "accessToken";

    public static final String TOKEN_TTL = "tokenTtl";

    public static final String GLOBAL_ADMIN = "globalAdmin";

    public static final String USERNAME = "username";

    public static final String ENCODE = "UTF-8";

    public static final String DATALINK_HOME = System.getProperty("datalink.home");

    public static final String USER_DIR = System.getProperty("user.dir");

    public static final String BASE_DIR = StringUtils.isEmpty(DATALINK_HOME) ? USER_DIR : DATALINK_HOME;

    public static final String STORAGE_PATH = Paths.get(BASE_DIR, "data").toString();

    public static final String PLUGIN_FILE_PATH = Paths.get(BASE_DIR, "plugin").toString();

    public static final String BACKUP_FILE_PATH = Paths.get(BASE_DIR, "backup").toString();

}
