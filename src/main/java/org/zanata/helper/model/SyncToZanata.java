package org.zanata.helper.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.zanata.helper.util.CronHelper;

import java.util.Date;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
@AllArgsConstructor
@Getter
@ToString
public class SyncToZanata implements Sync {
    private Long id;
    private String sourceRepositoryUrl;
    private String zanataVersionUrl;

    /**
     * see http://en.wikipedia.org/wiki/Cron#CRON_expression
     */
    private String cron = CronHelper.CronType.FIVE_MINUTES.getExpression();
    private Date lastExecuted;

    @Override
    public Type getType() {
        return Type.SYNC_TO_ZANATA;
    }
}
