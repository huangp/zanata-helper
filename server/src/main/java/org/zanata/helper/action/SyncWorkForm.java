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
public class SyncWorkForm implements Serializable {
    public final static String repoSettingsPrefix = "sourceSettingsConfig.";
    public final static String transSettingsPrefix = "transSettingsConfig.";

    @Size(min = 5, max = 100)
    @NotEmpty
    @Setter
    private String name;

    @Size(max = 255)
    @Setter
    private String description;

    @Size(max = 50)
    @Setter
    private String syncToServerCron;

    @Setter
    private SyncOption syncToServerOption = SyncOption.SOURCE;

    @Size(max = 50)
    @Setter
    private String syncToRepoCron;

    @Setter
    private SyncOption syncToRepoOption = SyncOption.BOTH;

    @NotEmpty
    @Size(max = 255)
    @Setter
    private String srcRepoPluginName;

    @NotEmpty
    @Size(max = 255)
    @Setter
    private String transServerPluginName;

    /**
     * All field id must prefix with {@link repoSettingsPrefix}
     */
    @Setter
    private Map<String, String> srcRepoConfig = new HashMap<>();

    /**
     * All field id must prefix with {@link transSettingsPrefix}
     */
    @Setter
    private Map<String, String> transServerConfig = new HashMap<>();

    public static String getRepoSettingsPrefix() {
        return repoSettingsPrefix;
    }

    public static String getTransSettingsPrefix() {
        return transSettingsPrefix;
    }
}
