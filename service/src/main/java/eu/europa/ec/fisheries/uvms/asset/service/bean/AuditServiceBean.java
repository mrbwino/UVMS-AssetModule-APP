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
package eu.europa.ec.fisheries.uvms.asset.service.bean;

import javax.ejb.Stateless;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.europa.ec.fisheries.uvms.asset.message.ModuleQueue;
import eu.europa.ec.fisheries.uvms.asset.message.exception.AssetMessageException;
import eu.europa.ec.fisheries.uvms.asset.message.mapper.AuditModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.asset.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.audit.model.exception.AuditModelMarshallException;
import eu.europa.ec.fisheries.uvms.entity.model.AssetSE;

@Stateless
public class AuditServiceBean {

    private static final Logger LOG = LoggerFactory.getLogger(AuditServiceBean.class);
    
    @Inject
    private MessageProducer messageProducer;
    
    public void logAssetCreated(AssetSE asset, String username) {
        try {
            String auditData = AuditModuleRequestMapper.mapAuditLogAssetCreated(asset.getId().toString(),
                    username);
            messageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
        } catch (AssetMessageException e) {
            LOG.warn("Failed to send audit log message! Asset with guid {} was created ", asset.getId());
        } catch (AuditModelMarshallException e) {
            LOG.error("Failed to send audit log message! Asset with guid {} was created ", asset.getId());
        }
    }
    
    public void logAssetUpdated(AssetSE asset, String comment, String username) {
        try {
            String auditData = AuditModuleRequestMapper.mapAuditLogAssetUpdated(asset.getId().toString(), comment,
                    username);
            messageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
        } catch (AuditModelMarshallException | AssetMessageException e) {
            LOG.error("Failed to send audit log message! Asset with guid {} was updated ",
                    asset.getId().toString());
        }
    }

    public void logAssetArchived(AssetSE asset, String comment, String username) {
        try {
            String auditData = AuditModuleRequestMapper.mapAuditLogAssetArchived(asset.getId().toString(), comment,
                    username);
            messageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
        } catch (AuditModelMarshallException | AssetMessageException e) {
            LOG.error("Failed to send audit log message! Asset with guid {} was archived ",
                    asset.getId().toString());
        }
    }
}