package eu.europa.ec.fisheries.uvms.tests.mobileterminal.service.arquillian;

import eu.europa.ec.fisheries.uvms.asset.domain.dao.AssetDao;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.ChannelDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.Channel;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.tests.TransactionalTests;
import eu.europa.ec.fisheries.uvms.tests.asset.service.arquillian.arquillian.AssetTestsHelper;
import eu.europa.ec.fisheries.uvms.tests.mobileterminal.service.arquillian.helper.TestPollHelper;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;


import javax.ejb.EJB;
import javax.inject.Inject;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class ChannelDaoIntTest extends TransactionalTests {

    @Inject
    private ChannelDaoBean channelDao;

    @Inject
    private AssetDao assetDao;

    @EJB
    private TestPollHelper testPollHelper;

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollableListSearch() throws Exception {
        // If this test is run on a clean DB it will do most of its things b4 the background plugin poll has
        // had time to do its work, thus giving the created MT inactivated values that can not be searched for
        Thread.sleep(2000);

        //Given - need a string list of id's.
        Asset asset = assetDao.createAsset(AssetTestsHelper.createBasicAsset());
        String id1 = asset.getId().toString();
        String id2 = UUID.randomUUID().toString();//"test_id2";
        List<String> idList = Arrays.asList(id1, id2);

        MobileTerminal mobileTerminal = testPollHelper.createAndPersistMobileTerminal(asset);
        assertNotNull(mobileTerminal.getId());

        List<Channel> channels = channelDao.getPollableListSearch(idList);

        assertNotNull(channels);
        assertEquals(1, channels.size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollableListSearch_emptyList() {
        List<String> emptyList = new ArrayList<>();

        List<Channel> channels = channelDao.getPollableListSearch(emptyList);

        assertNotNull(channels);
        assertEquals(0, channels.size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollableListSearch_NULL() {
        List<String> nullAsList = null;

        List<Channel> channels = channelDao.getPollableListSearch(nullAsList);

        assertNotNull(channels);
        assertEquals(0, channels.size());
    }
}