package eu.europa.ec.fisheries.uvms.rest.mobileterminal.rest.service;

import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.Plugin;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.PluginCapability;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.PluginCapabilityType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.PluginService;
import eu.europa.ec.fisheries.uvms.rest.asset.AbstractAssetRestTest;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.error.MTResponseCode;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
@RunAsClient
public class PluginRestResourceTest extends AbstractAssetRestTest {

    @Test
    public void upsertPluginTest() throws Exception{
        List<PluginService> pluginList = createPluginList();


        String response = getWebTarget()
                .path("/plugin")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(pluginList), String.class);

        assertEquals(MTResponseCode.OK.getCode(), getReturnCode(response));

        Plugin[] output = deserializeResponseDto(response, Plugin[].class);
        List<Plugin> outputList = Arrays.asList(output);

        assertEquals(2, output.length);
        assertTrue(response.contains("eu.europa.ec.fisheries.uvms.plugins.inmarTEST"));
        assertTrue(response.contains("eu.europa.ec.fisheries.uvms.plugins.inmarsat"));
    }

    private List<PluginService> createPluginList() {
        List<PluginService> returnList = new ArrayList<>();

        PluginService pluginService = new PluginService();
        pluginService.setInactive(false);
        pluginService.setLabelName("Thrane&Thrane&Test");
        pluginService.setSatelliteType("INMARSAT_C");
        pluginService.setServiceName("eu.europa.ec.fisheries.uvms.plugins.inmarTEST");


        PluginCapability capability = new PluginCapability();
        capability.setName(PluginCapabilityType.POLLABLE);
        capability.setValue("TRUE");

        pluginService.getCapability().add(capability);

        returnList.add(pluginService);

        pluginService = new PluginService();
        pluginService.setInactive(false);
        pluginService.setLabelName("Thrane&Thrane");
        pluginService.setSatelliteType("INMARSAT_C");
        pluginService.setServiceName("eu.europa.ec.fisheries.uvms.plugins.inmarsat");
        pluginService.getCapability().add(capability);

        returnList.add(pluginService);
        return returnList;
    }
}