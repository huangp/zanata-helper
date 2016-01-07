package org.zanata.helper.action;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.zanata.helper.common.model.SyncOption;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Getter
@NoArgsConstructor
public class JobForm implements Serializable {
    private final int NAME_MIN = 5;
    private final int NAME_MAX = 100;
    private final int DESCRIPTION_MAX = 255;
    private final int CRON_MAX = 50;
    private final int SOURCE_REPO_NAME_MIN = 1;
    private final int SOURCE_REPO_NAME_MAX = 255;
    private final int TRAN_SERVER_NAME_MIN = 1;
    private final int TRAN_SERVER_NAME_MAX = 255;

    public final static String repoSettingsPrefix = "sourceSettingsConfig-";
    public final static String transSettingsPrefix = "transSettingsConfig-";

    @Size(min = NAME_MIN, max = NAME_MAX)
    @Setter
    private String name;

    @Size(max = DESCRIPTION_MAX)
    @Setter
    private String description;

    @Size(max = CRON_MAX)
    @Setter
    private String syncToServerCron;

    @Setter
    private SyncOption syncToServerOption = SyncOption.SOURCE;

    @Size(max = CRON_MAX)
    @Setter
    private String syncToRepoCron;

    @Setter
    private SyncOption syncToRepoOption = SyncOption.BOTH;

    @NotEmpty
    @Size(min = SOURCE_REPO_NAME_MIN, max = SOURCE_REPO_NAME_MAX)
    @Setter
    private String srcRepoPluginName;

    @NotEmpty
    @Size(min = TRAN_SERVER_NAME_MIN, max = TRAN_SERVER_NAME_MAX)
    @Setter
    private String transServerPluginName;

    /**
     * All field id must prefix with {@link repoSettingsPrefix}
     */
    @Setter
    private Map<String, String> srcRepoConfig =
        new HashMap<String, String>();

    /**
     * All field id must prefix with {@link transSettingsPrefix}
     */
    @Setter
    private Map<String, String> transServerConfig =
        new HashMap<String, String>();

    public static String getRepoSettingsPrefix() {
        return repoSettingsPrefix;
    }

    public static String getTransSettingsPrefix() {
        return transSettingsPrefix;
    }
}
