package eu.europa.ec.fisheries.uvms.asset.client;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Providers;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import eu.europa.ec.fisheries.uvms.asset.client.model.Asset;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetIdentifier;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetQuery;

@RunWith(Arquillian.class)
public class AssetClientTest extends AbstractClientTest {

    @Context
    Providers provider;
    
    @Inject
    AssetClient assetClient;

    @Test
    public void pingTest() {
        String response = assetClient.ping();
        assertThat(response, CoreMatchers.is("pong"));
    }

    @Test
    public void getAssetByGuidTest() {
        Asset upsertAsset = assetClient.upsertAsset(AssetHelper.createBasicAsset());

        Asset asset = assetClient.getAssetById(AssetIdentifier.GUID, upsertAsset.getId().toString());
        assertThat(asset, CoreMatchers.is(CoreMatchers.notNullValue()));
        assertThat(asset.getId(), CoreMatchers.is(upsertAsset.getId()));
    }
    
    @Test
    public void upsertAssetTest() {
        Asset upsertAsset = assetClient.upsertAsset(AssetHelper.createBasicAsset());
        assertThat(upsertAsset, CoreMatchers.is(CoreMatchers.notNullValue()));
    }
    
    @Test
    public void queryAssetsTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset upsertAsset = assetClient.upsertAsset(asset);
        AssetQuery assetQuery = new AssetQuery();
        assetQuery.setFlagState(Arrays.asList(asset.getFlagStateCode()));
        List<Asset> assets = assetClient.getAssetList(assetQuery);
        assertTrue(assets.stream()
                .filter(a -> a.getId().equals(upsertAsset.getId()))
                .count() == 1);
    }
    
    @Test
    public void upsertAssetJMSTest() throws Exception {
        Asset asset = AssetHelper.createBasicAsset();
        assetClient.upsertAssetAsync(asset);
        Thread.sleep(5000); // Needed due to async call
        Asset fetchedAsset = assetClient.getAssetById(AssetIdentifier.CFR, asset.getCfr());
        assertThat(fetchedAsset.getCfr(), CoreMatchers.is(asset.getCfr()));
    }
}