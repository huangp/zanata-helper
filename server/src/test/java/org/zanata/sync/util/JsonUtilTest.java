package org.zanata.sync.util;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.zanata.sync.model.JobStatus;
import org.zanata.sync.model.JobStatusList;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
public class JsonUtilTest {

    @Test
    public void roundTripJsonTest() {
        JobStatusList list = new JobStatusList();
        list.add(0, JobStatus.EMPTY);
        String json = JsonUtil.toJson(list);

        JobStatusList returnList = JsonUtil.fromJson(json, JobStatusList.class);
        Assertions.assertThat(list).containsAll(returnList);
    }

    @Test
    public void toObjectTest() {
        String data = "[{\"status\":\"NORMAL\",\"lastStartTime\":\"2016-01-19T15:02:30.000+1000\",\"lastEndTime\":\"2016-01-19T15:02:48.722+1000\",\"nextStartTime\":\"2016-01-19T15:03:00.000+1000\"},{\"status\":\"NORMAL\",\"lastStartTime\":\"2016-01-19T15:02:00.000+1000\",\"lastEndTime\":\"2016-01-19T15:02:02.185+1000\",\"nextStartTime\":\"2016-01-19T15:02:30.000+1000\"}]";
        JobStatusList returnList = JsonUtil.fromJson(data, JobStatusList.class);
        Assertions.assertThat(returnList).hasSize(2);
    }
}
