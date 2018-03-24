package eu.europa.fisheries.uvms.asset.service.arquillian;

import eu.europa.ec.fisheries.uvms.asset.service.MDR_LiteService;
import eu.europa.ec.fisheries.uvms.entity.MDR_Lite;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.transaction.*;
import java.util.List;
import java.util.Random;


@RunWith(Arquillian.class)
public class MDR_LiteServiceIntTest extends TransactionalTests {


    Random rnd = new Random();


    @EJB
    MDR_LiteService service;

    private static final String CONSTANT = "testconstant";
    private static final String  CODE = "testcode";

    @Test
    @OperateOnDeployment("normal")
    public void create() {

        MDR_Lite createdMDR_lite = service.create(CONSTANT,CODE,CODE+"Description", "EXTRADATA");
        MDR_Lite fetchedMDR_lite = service.get(CONSTANT,CODE);
        Assert.assertNotNull(fetchedMDR_lite);
        service.delete(CONSTANT,CODE);
    }

    @Test
    @OperateOnDeployment("normal")
    public void tryToCreateDups() {

        MDR_Lite createdMDR_lite1 = service.create(CONSTANT,CODE,CODE+"Description", "EXTRADATA");
        MDR_Lite createdMDR_lite2 = service.create(CONSTANT,CODE,CODE+"Description", "EXTRADATA");
        List<MDR_Lite> rs = service.getAllFor(CONSTANT);
        Assert.assertEquals(rs.size(), 1);
        service.delete(CONSTANT,CODE);
    }

    @Test
    @OperateOnDeployment("normal")
    public void get() {

        MDR_Lite createdMDR_lite1 = service.create(CONSTANT,CODE,CODE+"Description", "EXTRADATA");
        MDR_Lite fetchedMDR_lite = service.get(CONSTANT,CODE);
        Assert.assertNotNull(fetchedMDR_lite);
        service.delete(CONSTANT,CODE);
    }


    @Test
    @OperateOnDeployment("normal")
    public void exists() {

        MDR_Lite createdMDR_lite1 = service.create(CONSTANT,CODE,CODE+"Description", "EXTRADATA");
        Boolean exist = service.exists(CONSTANT,CODE);
        Assert.assertNotNull(exist);
        service.delete(CONSTANT,CODE);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAllFor() {

        for (int i = 0; i < 10; i++) {
            String iStr = String.valueOf(i);
            service.create(CONSTANT,CODE+iStr,CODE+"Description", "EXTRADATA");
        }

        for (int i = 0; i < 10; i++) {
            String iStr = String.valueOf(i);
            service.create(CONSTANT+"2",CODE+iStr,CODE+"Description", "EXTRADATA");
        }

        List<MDR_Lite> rs1 = service.getAllFor(CONSTANT);
        List<MDR_Lite> rs2 = service.getAllFor(CONSTANT + "2");
        Assert.assertEquals(rs1.size(), 10);
        Assert.assertEquals(rs2.size(), 10);

        service.deleteAllFor(CONSTANT + "2");

        rs1 = service.getAllFor(CONSTANT);
        rs2 = service.getAllFor(CONSTANT + "2");
        Assert.assertEquals(rs1.size(), 10);
        Assert.assertEquals(rs2.size(), 0);

        service.deleteAllFor(CONSTANT);

        rs1 = service.getAllFor(CONSTANT);
        rs2 = service.getAllFor(CONSTANT+"2");
        Assert.assertEquals(rs1.size(), 0);
        Assert.assertEquals(rs2.size(), 0);
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateDescription() throws HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

        MDR_Lite created_record = service.create(CONSTANT,CODE,CODE+"Description", "EXTRADATA");
        String createdDescription = created_record.getDescription();

        created_record.setDescription("CHANGED");
        service.update(created_record.getConstant(), created_record.getCode(),"CHANGED",null);
        userTransaction.commit();
        userTransaction.begin();

        MDR_Lite fetched_record = service.get(created_record.getConstant(), created_record.getCode());

        Assert.assertNotEquals(createdDescription,fetched_record.getDescription());
        Assert.assertEquals("CHANGED",fetched_record.getDescription());

        service.deleteAllFor(CONSTANT);
    }





}