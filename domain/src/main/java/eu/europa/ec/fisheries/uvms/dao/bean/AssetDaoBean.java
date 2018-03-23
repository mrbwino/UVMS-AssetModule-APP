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
package eu.europa.ec.fisheries.uvms.dao.bean;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.*;

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException;
import eu.europa.ec.fisheries.uvms.dao.Dao;
import eu.europa.ec.fisheries.uvms.dao.exception.NoAssetEntityFoundException;
import eu.europa.ec.fisheries.uvms.entity.model.AssetEntity;
import eu.europa.ec.fisheries.uvms.entity.model.FlagState;
import eu.europa.ec.fisheries.uvms.entity.model.NotesActivityCode;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetId;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.uvms.constant.UvmsConstants;
import eu.europa.ec.fisheries.uvms.dao.AssetDao;
import eu.europa.ec.fisheries.uvms.entity.model.AssetHistory;
import eu.europa.ec.fisheries.uvms.mapper.SearchFieldMapper;
import eu.europa.ec.fisheries.uvms.mapper.SearchFieldType;
import eu.europa.ec.fisheries.uvms.mapper.SearchKeyValue;

import static eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType.INTERNAL_ID;

/**
 **/
@Stateless
public class AssetDaoBean extends Dao implements AssetDao {

    private static final Logger LOG = LoggerFactory.getLogger(AssetDaoBean.class);

    @Override
    public AssetEntity createAsset(AssetEntity asset) throws AssetDaoException {
        try {
            em.persist(asset);
            return asset;
        } catch (Exception e) {
            LOG.error("[ Error when creating asset. ] ");
            throw new AssetDaoException("[ Error when creating asset ] " + e.getMessage());
        }
    }

    @Override
    public AssetEntity getAssetById(Long id) throws AssetDaoException {
        try {
            return em.find(AssetEntity.class, id);
        } catch (NoResultException e) {
            throw new NoAssetEntityFoundException("No asset found for " + id);
        }
    }

    @Override
    public AssetEntity updateAsset(AssetEntity asset) throws AssetDaoException {
        try {
            em.merge(asset);
            em.flush();
            return asset;
        } catch (Exception e) {
            LOG.error("[ Error when updating asset. ] ");
            throw new AssetDaoException("[ update asset, id: " + asset.getId() + " ] " + e.getMessage());
        }
    }

    @Override
    public void deleteAsset(AssetEntity assetEntity) throws AssetDaoException {

        if (assetEntity == null) {
            // does not destroy anything so just log and return
            LOG.debug("deleteAsset. assetEntity is null. check you code");
            return;
        }
        em.remove(assetEntity);
    }

    @Override
    public List<AssetEntity> getAssetListAll() throws AssetDaoException {
        try {
            TypedQuery<AssetEntity> query = em.createNamedQuery(UvmsConstants.ASSET_FIND_ALL, AssetEntity.class);
            return query.getResultList();
        } catch (IllegalArgumentException e) {
            LOG.error("[ Error when getting asset list. ] ");
            throw new AssetDaoException("[ get all asset ] " + e.getMessage());
        }
    }

    @Override
    public AssetEntity getAssetByCfr(String cfr) throws NoAssetEntityFoundException, AssetDaoException {
        try {
            TypedQuery<AssetEntity> query = em.createNamedQuery(UvmsConstants.ASSET_FIND_BY_CFR, AssetEntity.class);
            query.setParameter("cfr", cfr);
            AssetEntity singleResult = query.getSingleResult();
            return singleResult;
        } catch (NoResultException e) {
            throw new NoAssetEntityFoundException("No asset found for " + cfr);
        }
    }

    @Override
    public AssetEntity getAssetByIrcs(String ircs) throws NoAssetEntityFoundException, AssetDaoException {
        try {
            TypedQuery<AssetEntity> query = em.createNamedQuery(UvmsConstants.ASSET_FIND_BY_IRCS, AssetEntity.class);
            query.setParameter("ircs", ircs);
            AssetEntity singleResult = query.getSingleResult();
            return singleResult;
        } catch (NoResultException e) {
            throw new NoAssetEntityFoundException("No asset found for " + ircs);
        }
    }

    @Override
    public AssetEntity getAssetByGuid(String guid) throws AssetDaoException {
        try {
            TypedQuery<AssetEntity> query = em.createNamedQuery(UvmsConstants.ASSET_FIND_BY_GUID, AssetEntity.class);
            query.setParameter("guid", guid);
            AssetEntity singleResult = query.getSingleResult();
            return singleResult;
        } catch (NoResultException e) {
            throw new NoAssetEntityFoundException("No asset found for " + guid);
        }
    }

    @Override
    public AssetEntity getAssetByImo(String imo) throws AssetDaoException {
        try {
            TypedQuery<AssetEntity> query = em.createNamedQuery(UvmsConstants.ASSET_FIND_BY_IMO, AssetEntity.class);
            query.setParameter("imo", imo);
            AssetEntity singleResult = query.getSingleResult();
            return singleResult;
        } catch (NoResultException e) {
            throw new NoAssetEntityFoundException("No asset found for " + imo);
        }
    }

    @Override
    public AssetEntity getAssetByMmsi(String mmsi) throws AssetDaoException {
        try {
            TypedQuery<AssetEntity> query = em.createNamedQuery(UvmsConstants.ASSET_FIND_BY_MMSI, AssetEntity.class);
            query.setParameter("mmsi", mmsi);
            AssetEntity singleResult = query.getSingleResult();
            return singleResult;
        } catch (NoResultException e) {
            throw new NoAssetEntityFoundException("No asset found for " + mmsi);
        }
    }

    @Override
    public AssetHistory getAssetHistoryByGuid(String guid) throws AssetDaoException {
        try {
            TypedQuery<AssetHistory> query = em.createNamedQuery(UvmsConstants.ASSETHISTORY_FIND_BY_GUID, AssetHistory.class);
            query.setParameter("guid", guid);
            AssetHistory singleResult = query.getSingleResult();
            return singleResult;
        } catch (NoResultException e) {
            throw new NoAssetEntityFoundException("No asset history found for " + guid);
        }
    }

    @Override
    public Long getAssetCount(String sql, List<SearchKeyValue> searchFields, boolean isDynamic) throws AssetDaoException {
        TypedQuery<Long> query = em.createQuery(sql, Long.class);

        for (SearchKeyValue field : searchFields) {
            if (SearchFieldMapper.useLike(field)) {
                int containsCount = 0;
                for (String searchValue : field.getSearchValues()) {
                    containsCount++;
                    String tmpValue = searchValue.replace("*", "");
                    query.setParameter(field.getSearchField().getValueName() + containsCount, "%" + tmpValue + "%");
                }
            } else {
                if (field.getSearchField().getFieldType().equals(SearchFieldType.NUMBER)) {
                    List<Integer> parameter = new ArrayList<>();
                    for (String param : field.getSearchValues()) {
                        parameter.add(Integer.parseInt(param));
                    }
                    query.setParameter(field.getSearchField().getValueName(), parameter);
                } else if (field.getSearchField().getFieldType().equals(SearchFieldType.LIST)) {
                    query.setParameter(field.getSearchField().getValueName(), field.getSearchValues());
                } else if (field.getSearchField().getFieldType().equals(SearchFieldType.BOOLEAN)) { //BOOLEAN only one value
                    query.setParameter(field.getSearchField().getValueName(), Boolean.parseBoolean(field.getSearchValues().get(0)));
                } else { //DECIMAL, only one value
                    query.setParameter(field.getSearchField().getValueName(), new BigDecimal(field.getSearchValues().get(0)));
                }
            }
        }

        return query.getSingleResult();
    }

    @Override
    public List<AssetHistory> getAssetListSearchPaginated(Integer pageNumber, Integer pageSize, String sql, List<SearchKeyValue> searchFields, boolean isDynamic) throws AssetDaoException {
        TypedQuery<AssetHistory> query = getVesselHistoryQuery(sql, searchFields, isDynamic);
        query.setFirstResult(pageSize * (pageNumber - 1));
        query.setMaxResults(pageSize);
        return query.getResultList();
    }

    @Override
    public List<AssetHistory> getAssetListSearchNotPaginated(String sql, List<SearchKeyValue> searchFields, boolean isDynamic) throws AssetDaoException {
        return getVesselHistoryQuery(sql, searchFields, isDynamic).getResultList();
    }

    private TypedQuery<AssetHistory> getVesselHistoryQuery(String sql, List<SearchKeyValue> searchFields, boolean isDynamic) {
        TypedQuery<AssetHistory> query = em.createQuery(sql, AssetHistory.class);

        for (SearchKeyValue field : searchFields) {
            if (SearchFieldMapper.useLike(field)) {
                int containsCount = 0;
                for (String searchValue : field.getSearchValues()) {
                    containsCount++;
                    String tmpValue = searchValue.replace("*", "");
                    query.setParameter(field.getSearchField().getValueName() + containsCount, "%" + tmpValue + "%");
                }
            } else {
                if (field.getSearchField().getFieldType().equals(SearchFieldType.NUMBER)) {
                    List<Integer> parameter = new ArrayList<>();
                    for (String param : field.getSearchValues()) {
                        parameter.add(Integer.parseInt(param));
                    }
                    query.setParameter(field.getSearchField().getValueName(), parameter);
                } else if (field.getSearchField().getFieldType().equals(SearchFieldType.LIST)) {
                    query.setParameter(field.getSearchField().getValueName(), field.getSearchValues());
                } else if (field.getSearchField().getFieldType().equals(SearchFieldType.BOOLEAN)) { //BOOLEAN only one value
                    query.setParameter(field.getSearchField().getValueName(), Boolean.parseBoolean(field.getSearchValues().get(0)));
                } else { //DECIMAL, only one value
                    query.setParameter(field.getSearchField().getValueName(), new BigDecimal(field.getSearchValues().get(0)));
                }
            }
        }
        return query;
    }

    @Override
    public List<AssetHistory> getAssetListByAssetGuids(List<String> assetGuids) throws AssetDaoException {
        try {
            TypedQuery<AssetHistory> query = em.createNamedQuery(UvmsConstants.ASSETHISTORY_FIND_BY_GUIDS, AssetHistory.class);
            query.setParameter("guids", assetGuids);
            return query.getResultList();
        } catch (IllegalArgumentException e) {
            throw new AssetDaoException("[ get all asset ] " + e.getMessage());
        }
    }

    @Override
    public AssetEntity getAssetByCfrExcludeArchived(String cfr) throws NoAssetEntityFoundException, AssetDaoException {
        try {
            TypedQuery<AssetEntity> query = em.createNamedQuery(UvmsConstants.ASSET_FIND_BY_CFR_EXCLUDE_ARCHIVED, AssetEntity.class);
            query.setParameter("cfr", cfr);
            AssetEntity singleResult = query.getSingleResult();
            return singleResult;
        } catch (NoResultException e) {
            throw new NoAssetEntityFoundException("No asset found for " + cfr);
        }
    }

    @Override
    public AssetEntity getAssetByIrcsExcludeArchived(String ircs) throws NoAssetEntityFoundException, AssetDaoException {
        try {
            TypedQuery<AssetEntity> query = em.createNamedQuery(UvmsConstants.ASSET_FIND_BY_IRCS_EXCLUDE_ARCHIVED, AssetEntity.class);
            query.setParameter("ircs", ircs);
            AssetEntity singleResult = query.getSingleResult();
            return singleResult;
        } catch (NoResultException e) {
            throw new NoAssetEntityFoundException("No asset found for " + ircs);
        }
    }

    @Override
    public AssetEntity getAssetByImoExcludeArchived(String imo) throws AssetDaoException {
        try {
            TypedQuery<AssetEntity> query = em.createNamedQuery(UvmsConstants.ASSET_FIND_BY_IMO_EXCLUDE_ARCHIVED, AssetEntity.class);
            query.setParameter("imo", imo);
            AssetEntity singleResult = query.getSingleResult();
            return singleResult;
        } catch (NoResultException e) {
            throw new NoAssetEntityFoundException("No asset found for " + imo);
        }
    }

    @Override
    public AssetEntity getAssetByMmsiExcludeArchived(String mmsi) throws AssetDaoException {
        try {
            TypedQuery<AssetEntity> query = em.createNamedQuery(UvmsConstants.ASSET_FIND_BY_MMSI_EXCLUDE_ARCHIVED, AssetEntity.class);
            query.setParameter("mmsi", mmsi);
            AssetEntity singleResult = query.getSingleResult();
            return singleResult;
        } catch (NoResultException e) {
            throw new NoAssetEntityFoundException("No asset found for " + mmsi);
        }
    }

    @Override
    public List<NotesActivityCode> getNoteActivityCodes() {
        TypedQuery<NotesActivityCode> query = em.createNamedQuery(UvmsConstants.ASSET_NOTE_ACTIVITY_CODE_FIND_ALL, NotesActivityCode.class);
        return query.getResultList();
    }

    @Override
    public AssetEntity getAssetByIccat(String iccat) throws AssetDaoException {
        try {
            TypedQuery<AssetEntity> query = em.createNamedQuery(UvmsConstants.ASSET_FIND_BY_ICCAT, AssetEntity.class);
            query.setParameter("iccat", iccat);
            return query.getSingleResult();
        } catch (NoResultException e) {
            throw new NoAssetEntityFoundException("No asset found for " + iccat);
        }
    }

    @Override
    public AssetEntity getAssetByUvi(String uvi) throws AssetDaoException {
        try {
            TypedQuery<AssetEntity> query = em.createNamedQuery(UvmsConstants.ASSET_FIND_BY_UVI, AssetEntity.class);
            query.setParameter("uvi", uvi);
            return query.getSingleResult();
        } catch (NoResultException e) {
            throw new NoAssetEntityFoundException("No asset found for " + uvi);
        }
    }

    @Override
    public AssetEntity getAssetByGfcm(String gfcm) throws AssetDaoException {
        try {
            TypedQuery<AssetEntity> query = em.createNamedQuery(UvmsConstants.ASSET_FIND_BY_GFCM, AssetEntity.class);
            query.setParameter("gfcm", gfcm);
            return query.getSingleResult();
        } catch (NoResultException e) {
            throw new NoAssetEntityFoundException("No asset found for " + gfcm);
        }
    }

    private Date lowerResolution(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();

    }

    @Override
    public FlagState getAssetFlagStateByIdAndDate(String assetGuid, Date date) throws AssetDaoException {

        Date d = lowerResolution(date);

        String hql = "select ah.countryOfRegistration from AssetHistory ah where ah.asset.guid = :guid and ah.dateOfEvent <= :dateofevent order by ah.dateOfEvent DESC";
        Query q = em.createQuery(hql);
        q.setParameter("guid", assetGuid);
        q.setParameter("dateofevent", d);
        q.setMaxResults(1);
        try {
            String countryOfRegistration = (String) q.getSingleResult();
            TypedQuery<FlagState> query_flagstate = em.createNamedQuery(UvmsConstants.FLAGSTATE_GET_BY_CODE, FlagState.class);
            query_flagstate.setParameter("code", countryOfRegistration);
            FlagState flagState = query_flagstate.getSingleResult();
            return flagState;
        } catch (NoResultException ex) {
            // Throw user exception
            String msg = "This code was not found in database. Check your setup";
            throw new AssetDaoException(msg);
        }
    }

    @Override
    public AssetEntity getAssetFromAssetIdAndDate(AssetId assetId, Date date) throws AssetDaoException {

        Date d = lowerResolution(date);
        AssetIdType assetIdType = assetId.getType();
        String keyval = assetId.getValue();

        String hql = "select ah.asset from AssetHistory ah where %s = :keyval and ah.dateOfEvent <= :date order by ah.dateOfEvent DESC";
        switch (assetIdType) {
            case INTERNAL_ID:
                break;
            case CFR:
                hql = String.format(hql, "ah.cfr");
                break;
            case IRCS:
                hql = String.format(hql, "ah.ircs");
                break;
            case IMO:
                hql = String.format(hql, "ah.imo");
                break;
            case MMSI:
                hql = String.format(hql, "ah.mmsi");
                break;
            case GUID:
                hql = String.format(hql, "ah.guid");
                break;
            case ICCAT:
                hql = String.format(hql, "ah.iccat");
                break;
            case UVI:
                hql = String.format(hql, "ah.uvi");
                break;
            case GFCM:
                hql = String.format(hql, "ah.gfcm");
                break;
            default:
                throw new AssetDaoException("Could not create query. Check your code AssetIdType is invalid");
        }
        Query q = em.createQuery(hql);
        q.setParameter("keyval", keyval);
        q.setParameter("date", d);
        q.setMaxResults(1);
        try {
            List<AssetEntity> assetEntityList = q.getResultList();
            if (assetEntityList.size() > 0) {
                return assetEntityList.get(0);
            } else {
                throw new AssetDaoException("Nothing in resultset for query " + hql);
            }
        } catch (NoResultException ex) {
            throw new AssetDaoException(ex.toString());
        }
    }


}