/*
﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.fisheries.uvms.tests.mobileterminal.service.arquillian;

import static org.junit.Assert.assertThat;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import javax.inject.Inject;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.ListCriteria;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollListQuery;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollListResponse;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollResponseType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollSearchCriteria;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.SearchKey;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.ListPagination;
import eu.europa.ec.fisheries.uvms.asset.domain.dao.AssetDao;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.mobileterminal.bean.PollServiceBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.PollProgramDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.PollProgram;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.PollStateEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.timer.PollTimerTask;
import eu.europa.fisheries.uvms.tests.TransactionalTests;
import eu.europa.fisheries.uvms.tests.asset.service.arquillian.arquillian.AssetTestsHelper;
import eu.europa.fisheries.uvms.tests.mobileterminal.service.arquillian.helper.TestPollHelper;

@RunWith(Arquillian.class)
public class PollTimerTaskTest extends TransactionalTests {
    
    @Inject
    TestPollHelper pollHelper;
    
    @Inject
    AssetDao assetDao;
    
    @Inject
    PollServiceBean pollService;
    
    @Inject
    PollProgramDaoBean pollDao;
    
    @Test
    @OperateOnDeployment("normal")
    public void runPollTimerTaskPollShouldBeCreated() throws Exception {
        
        Asset asset = assetDao.createAsset(AssetTestsHelper.createBasicAsset());
        PollProgram pollProgram = pollHelper.createPollProgramHelper(asset.getId().toString(), OffsetDateTime.now(ZoneOffset.UTC).minusHours(1), OffsetDateTime.now(ZoneOffset.UTC).plusHours(1), null);
        pollDao.createPollProgram(pollProgram);
        
        new PollTimerTask(pollService).run();
        
        List<PollResponseType> polls = getAllPolls(asset.getId());
        
        assertThat(polls.size(), CoreMatchers.is(1));
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void runPollTimerTaskTwoPollsShouldBeCreated() throws Exception {
        
        Asset asset = assetDao.createAsset(AssetTestsHelper.createBasicAsset());
        PollProgram pollProgram = pollHelper.createPollProgramHelper(asset.getId().toString(), OffsetDateTime.now(ZoneOffset.UTC).minusHours(1), OffsetDateTime.now(ZoneOffset.UTC).plusHours(1), null);
        pollDao.createPollProgram(pollProgram);
        
        new PollTimerTask(pollService).run();
        pollProgram.setLatestRun(OffsetDateTime.now(ZoneOffset.UTC).minusMinutes(1)); // Frequency is 1s
        pollDao.updatePollProgram(pollProgram);
        new PollTimerTask(pollService).run();
        
        List<PollResponseType> polls = getAllPolls(asset.getId());
        
        assertThat(polls.size(), CoreMatchers.is(2));
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void runPollTimerTaskFutureDateShouldNotCreatePoll() throws Exception {
        
        Asset asset = assetDao.createAsset(AssetTestsHelper.createBasicAsset());
        PollProgram pollProgram = pollHelper.createPollProgramHelper(asset.getId().toString(), OffsetDateTime.now(ZoneOffset.UTC).plusHours(1), OffsetDateTime.now(ZoneOffset.UTC).plusHours(2), null);
        pollDao.createPollProgram(pollProgram);
        
        new PollTimerTask(pollService).run();
        
        List<PollResponseType> polls = getAllPolls(asset.getId());
        
        assertThat(polls.size(), CoreMatchers.is(0));
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void runPollTimerTaskLastRunShoulBeSet() throws Exception {
        
        Asset asset = assetDao.createAsset(AssetTestsHelper.createBasicAsset());
        PollProgram pollProgram = pollHelper.createPollProgramHelper(asset.getId().toString(), OffsetDateTime.now(ZoneOffset.UTC).minusHours(1), OffsetDateTime.now(ZoneOffset.UTC).plusHours(1), null);
        pollDao.createPollProgram(pollProgram);
        
        new PollTimerTask(pollService).run();
        
        PollProgram fetchedPollProgram = pollDao.getPollProgramByGuid(pollProgram.getId().toString());
        assertThat(fetchedPollProgram.getLatestRun(), CoreMatchers.is(CoreMatchers.notNullValue()));
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void runPollTimerTaskOldProgramShouldBeArchived() throws Exception {
        
        Asset asset = assetDao.createAsset(AssetTestsHelper.createBasicAsset());
        PollProgram pollProgram = pollHelper.createPollProgramHelper(asset.getId().toString(), OffsetDateTime.now(ZoneOffset.UTC).minusHours(2), OffsetDateTime.now(ZoneOffset.UTC).minusHours(1), null);
        pollDao.createPollProgram(pollProgram);
        
        new PollTimerTask(pollService).run();
        
        PollProgram fetchedPollProgram = pollDao.getPollProgramByGuid(pollProgram.getId().toString());
        assertThat(fetchedPollProgram.getPollState(), CoreMatchers.is(PollStateEnum.ARCHIVED));
    }
    
    private List<PollResponseType> getAllPolls(UUID connectId) {
        PollListQuery query = new PollListQuery();
        ListPagination pagination = new ListPagination();
        pagination.setListSize(1000);
        pagination.setPage(1);
        query.setPagination(pagination);
        PollSearchCriteria criterias = new PollSearchCriteria();
        criterias.setIsDynamic(true);
        ListCriteria criteria = new ListCriteria();
        criteria.setKey(SearchKey.CONNECT_ID);
        criteria.setValue(connectId.toString());
        criterias.getCriterias().add(criteria);
        query.setPollSearchCriteria(criterias);
        PollListResponse pollList = pollService.getPollList(query);
        return pollList.getPollList();
    }
}
