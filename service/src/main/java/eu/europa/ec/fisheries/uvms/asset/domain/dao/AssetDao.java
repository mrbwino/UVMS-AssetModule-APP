package eu.europa.ec.fisheries.uvms.asset.domain.dao;

import eu.europa.ec.fisheries.uvms.asset.domain.constant.AssetIdentifier;
import eu.europa.ec.fisheries.uvms.asset.domain.constant.SearchFields;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetRemapMapping;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.ContactInfo;
import eu.europa.ec.fisheries.uvms.asset.domain.mapper.*;
import eu.europa.ec.fisheries.uvms.asset.dto.MicroAsset;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.exception.AuditException;
import org.hibernate.envers.exception.RevisionDoesNotExistException;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.hibernate.envers.query.criteria.AuditCriterion;
import org.hibernate.envers.query.criteria.AuditDisjunction;
import org.hibernate.envers.query.criteria.ExtendableCriterion;
import org.hibernate.envers.query.criteria.MatchMode;

import javax.ejb.Stateless;
import javax.persistence.*;
import java.math.BigInteger;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
public class AssetDao {

    @PersistenceContext
    private EntityManager em;

    public Asset createAsset(Asset asset) {
        em.persist(asset);
        return asset;
    }

    public int getNextUnknownShipNumber() {
        Query query = em.createNativeQuery("select nextval('next_unknown_ship_seq')");
        return ((BigInteger) query.getSingleResult()).intValue();
    }

    public Asset getAssetById(UUID id) {
        return em.find(Asset.class, id);
    }

    public Asset getAssetByCfr(String cfr) {
        try {
            TypedQuery<Asset> query = em.createNamedQuery(Asset.ASSET_FIND_BY_CFR, Asset.class);
            query.setParameter("cfr", cfr);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Asset getAssetByIrcs(String ircs) {
        try {
            TypedQuery<Asset> query = em.createNamedQuery(Asset.ASSET_FIND_BY_IRCS, Asset.class);
            query.setParameter("ircs", ircs);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Asset getAssetByImo(String imo) {
        try {
            TypedQuery<Asset> query = em.createNamedQuery(Asset.ASSET_FIND_BY_IMO, Asset.class);
            query.setParameter("imo", imo);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Asset getAssetByMmsi(String mmsi) {
        try {
            TypedQuery<Asset> query = em.createNamedQuery(Asset.ASSET_FIND_BY_MMSI, Asset.class);
            query.setParameter("mmsi", mmsi);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Asset getAssetByIccat(String iccat) {
        try {
            TypedQuery<Asset> query = em.createNamedQuery(Asset.ASSET_FIND_BY_ICCAT, Asset.class);
            query.setParameter("iccat", iccat);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Asset getAssetByUvi(String uvi) {
        try {
            TypedQuery<Asset> query = em.createNamedQuery(Asset.ASSET_FIND_BY_UVI, Asset.class);
            query.setParameter("uvi", uvi);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Asset getAssetByGfcm(String gfcm) {
        try {
            TypedQuery<Asset> query = em.createNamedQuery(Asset.ASSET_FIND_BY_GFCM, Asset.class);
            query.setParameter("gfcm", gfcm);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Asset> getAssetByMmsiOrIrcs(String mmsi, String ircs){
        String correctedIrcs = (ircs != null && ircs.matches("\\w{3}\\d{4}") ? ircs.substring(0,3) + "-" + ircs.substring(3) : ircs);

        TypedQuery<Asset> query = em.createNamedQuery(Asset.ASSET_FIND_BY_MMSI_OR_IRCS, Asset.class);
        query.setParameter("mmsi", mmsi);
        query.setParameter("ircs", correctedIrcs);
        return query.getResultList();
    }

    public Asset updateAsset(Asset asset) {
        Asset updated = em.merge(asset);
        em.flush();
        return updated;
    }

    public void deleteAsset(Asset asset) {
        em.remove(em.contains(asset) ? asset : em.merge(asset));
    }

    public List<Asset> getAssetListAll() {
        TypedQuery<Asset> query = em.createNamedQuery(Asset.ASSET_FIND_ALL, Asset.class);
        return query.getResultList();
    }

    public List<String> getAllAvailableVesselTypes(){
        TypedQuery<String> query = em.createNamedQuery(Asset.ASSET_ALL_AVAILABLE_VESSEL_TYPES, String.class);
        return query.getResultList();
    }

    /*public Long getAssetCount(List<SearchKeyValue> searchFields, Boolean isDynamic, boolean includeInactivated) {
        try {
            AuditQuery query = createAuditQuery(searchFields, isDynamic, includeInactivated);
            return (Long) query.addProjection(AuditEntity.id().count()).getSingleResult();
        } catch (AuditException e) {
            return 0L;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Asset> getAssetListSearchPaginated(Integer pageNumber, Integer pageSize, List<SearchKeyValue> searchFields,
                                                   boolean isDynamic, boolean includeInactivated) {
        try {
            AuditQuery query = createAuditQuery(searchFields, isDynamic, includeInactivated);
            query.setFirstResult(pageSize * (pageNumber - 1));
            query.setMaxResults(pageSize);
            return query.getResultList();
        } catch (AuditException e) {
            return Collections.emptyList();
        }
    }*/

    private AuditQuery createAuditQuery(List<SearchKeyValue> searchFields, boolean isDynamic, boolean includeInactivated) {
        AuditReader auditReader = AuditReaderFactory.get(em);

        AuditQuery query;
        SearchKeyValue dateSearchField = getDateSearchField(searchFields);
        if (dateSearchField != null) {
            Instant date = Instant.parse(dateSearchField.getSearchValues().get(0));
            Number revisionNumberForDate = auditReader.getRevisionNumberForDate(Date.from(date));
            query = auditReader.createQuery().forEntitiesAtRevision(Asset.class, revisionNumberForDate);
        } else {
            query = auditReader.createQuery().forRevisionsOfEntity(Asset.class, true, true);

            if (!searchRevisions(searchFields)) {
                query.add(AuditEntity.revisionNumber().maximize().computeAggregationInInstanceContext());
            }
            if(!includeInactivated) {
                query.add(AuditEntity.property("active").eq(true));
            }
        }

        ExtendableCriterion operator;
        if (isDynamic) {
            operator = AuditEntity.conjunction();           //and
        } else {
            operator = AuditEntity.disjunction();           //or
        }

        boolean operatorUsed = false;
        for (SearchKeyValue searchKeyValue : searchFields) {
            if (useLike(searchKeyValue)) {
                AuditDisjunction op = AuditEntity.disjunction();
                for (String value : searchKeyValue.getSearchValues()) {
                    op.add(AuditEntity.property(searchKeyValue.getSearchField().getFieldName()).ilike(value.replace("*", "%").toLowerCase(), MatchMode.ANYWHERE));
                }
                operatorUsed = true;
                operator.add(op);
            } else if (searchKeyValue.getSearchField().getFieldType().equals(SearchFieldType.MIN_DECIMAL)) {
                operatorUsed = true;
                operator.add(AuditEntity.property(searchKeyValue.getSearchField().getFieldName()).ge(Double.valueOf(searchKeyValue.getSearchValues().get(0))));
            } else if (searchKeyValue.getSearchField().getFieldType().equals(SearchFieldType.MAX_DECIMAL)) {
                operatorUsed = true;
                operator.add(AuditEntity.property(searchKeyValue.getSearchField().getFieldName()).le(Double.valueOf(searchKeyValue.getSearchValues().get(0))));
            } else if (searchKeyValue.getSearchField().getFieldType().equals(SearchFieldType.LIST)) {
                operatorUsed = true;
                AuditDisjunction disjunctionOperator = AuditEntity.disjunction();
                for (String v : searchKeyValue.getSearchValuesAsLowerCase()) {
                    disjunctionOperator.add(AuditEntity.property(searchKeyValue.getSearchField().getFieldName()).ilike(v, MatchMode.ANYWHERE));
                }
                operator.add(disjunctionOperator);
            } else if (searchKeyValue.getSearchField().getFieldType().equals(SearchFieldType.NUMBER)) {
                List<Integer> intValues = searchKeyValue.getSearchValues().stream().map(Integer::parseInt).collect(Collectors.toList());
                operatorUsed = true;
                operator.add(AuditEntity.property(searchKeyValue.getSearchField().getFieldName()).in(intValues));
            } else if (searchKeyValue.getSearchField().getFieldType().equals(SearchFieldType.ID)) {
                List<UUID> ids = searchKeyValue.getSearchValues().stream().map(UUID::fromString).collect(Collectors.toList());
                operatorUsed = true;
                operator.add(AuditEntity.property(searchKeyValue.getSearchField().getFieldName()).in(ids));
            } else if (searchKeyValue.getSearchField().getFieldType().equals(SearchFieldType.BOOLEAN) ||
                    searchKeyValue.getSearchField().getFieldType().equals(SearchFieldType.STRING)) {
                operatorUsed = true;
                operator.add(AuditEntity.property(searchKeyValue.getSearchField().getFieldName()).eq(searchKeyValue.getSearchValues().get(0)));
            }
        }
        if (operatorUsed) {
            query.add((AuditCriterion) operator);
        }
        return query;
    }

    public Long getAssetCountAQ(Q queryTree, boolean includeInactivated) {
        try {
            AuditQuery query = createAuditQueryAQ(queryTree, includeInactivated);
            return (Long) query.addProjection(AuditEntity.id().count()).getSingleResult();
        } catch (AuditException e) {
            return 0L;
        }
    }

    public List<Asset> getAssetListSearchPaginatedAQ(Integer pageNumber, Integer pageSize, Q queryTree, boolean includeInactivated) {
        try {
            AuditQuery query = createAuditQueryAQ(queryTree, includeInactivated);
            query.setFirstResult(pageSize * (pageNumber - 1));
            query.setMaxResults(pageSize);
            List<Asset> test = query.getResultList();
            return test;
        } catch (AuditException e) {
            return Collections.emptyList();
        }
    }

    private AuditQuery createAuditQueryAQ(Q queryTree, boolean includeInactivated) {
        AuditReader auditReader = AuditReaderFactory.get(em);

        AuditQuery query;
        A dateSearchField = getDateSearchFieldAQ(queryTree);
        if (dateSearchField != null) {
            Instant date = DateUtils.stringToDate(dateSearchField.getSearchValue());
            Number revisionNumberForDate = auditReader.getRevisionNumberForDate(Date.from(date));
            query = auditReader.createQuery().forEntitiesAtRevision(Asset.class, revisionNumberForDate);
        } else {
            query = auditReader.createQuery().forRevisionsOfEntity(Asset.class, true, true);

            if (!searchRevisionsAQ(queryTree)) {
                query.add(AuditEntity.revisionNumber().maximize().computeAggregationInInstanceContext());
            }
            if(!includeInactivated) {
                query.add(AuditEntity.property("active").eq(true));
            }
        }

        AuditCriterion auditCriterion = queryBuilder(queryTree);
        if(auditCriterion != null) {
            query.add(auditCriterion);
        }
        return query;
    }

    private AuditCriterion queryBuilder(Q query){
        ExtendableCriterion operator;
        boolean operatorUsed = false;
        if(query.isLogicalAnd()){
            operator = AuditEntity.conjunction();           //and
        }else{
            operator = AuditEntity.disjunction();           //or
        }
        for (AQ field : query.getFields()) {
            if(!field.isLeaf()){
                AuditCriterion auditCriterion = queryBuilder((Q) field);
                if(auditCriterion != null){
                    operator.add(auditCriterion);
                    operatorUsed = true;
                }
            }else{
                A leaf = (A) field;
                if (leaf.getSearchValue().contains("*")) {
                    operator.add(AuditEntity.property(leaf.getSearchField().getFieldName()).ilike(leaf.getSearchValue().replace("*", "%").toLowerCase(), MatchMode.ANYWHERE));
                    operatorUsed = true;
                } else if (leaf.getSearchField().getFieldType().equals(SearchFieldType.MIN_DECIMAL)) {
                    operator.add(AuditEntity.property(leaf.getSearchField().getFieldName()).ge(Double.valueOf(leaf.getSearchValue())));
                    operatorUsed = true;
                } else if (leaf.getSearchField().getFieldType().equals(SearchFieldType.MAX_DECIMAL)) {
                    operator.add(AuditEntity.property(leaf.getSearchField().getFieldName()).le(Double.valueOf(leaf.getSearchValue())));
                    operatorUsed = true;
                } else if (leaf.getSearchField().getFieldType().equals(SearchFieldType.LIST)) {
                    operator.add(AuditEntity.property(leaf.getSearchField().getFieldName()).ilike(leaf.getSearchValue(), MatchMode.ANYWHERE));
                    operatorUsed = true;
                } else if (leaf.getSearchField().getFieldType().equals(SearchFieldType.NUMBER)) {
                    Integer intValue = Integer.parseInt(leaf.getSearchValue());
                    operator.add(AuditEntity.property(leaf.getSearchField().getFieldName()).eq(intValue));
                    operatorUsed = true;
                } else if (leaf.getSearchField().getFieldType().equals(SearchFieldType.ID)) {
                    UUID id = UUID.fromString(leaf.getSearchValue());
                    operator.add(AuditEntity.property(leaf.getSearchField().getFieldName()).eq(id));
                    operatorUsed = true;
                } else if (leaf.getSearchField().getFieldType().equals(SearchFieldType.BOOLEAN) ||
                        leaf.getSearchField().getFieldType().equals(SearchFieldType.STRING)) {
                    operator.add(AuditEntity.property(leaf.getSearchField().getFieldName()).eq(leaf.getSearchValue()));
                    operatorUsed = true;
                }
            }
        }
        if(operatorUsed) {
            return (AuditCriterion) operator;
        }
        return null;
    }

    private A getDateSearchFieldAQ(Q searchFields) {
        for (AQ field : searchFields.getFields()) {
            if(!field.isLeaf()){
                A leaf = getDateSearchFieldAQ((Q) field);
                if(leaf != null){
                    return leaf;
                }
            }else {
                A leaf = (A) field;
                if (leaf.getSearchField().equals(SearchFields.DATE)) {
                    return leaf;
                }
            }
        }
        return null;
    }

    private boolean searchRevisionsAQ(Q searchFields) {
        for (AQ field : searchFields.getFields()) {
            if(!field.isLeaf()){
                boolean leaf = searchRevisionsAQ((Q) field);
                if(leaf == true){
                    return true;
                }
            }else {
                A leaf = (A) field;
                if (leaf.getSearchField().equals(SearchFields.HIST_GUID)) {
                    return true;
                }
            }
        }
        return false;
    }


    private boolean searchRevisions(List<SearchKeyValue> searchFields) {
        for (SearchKeyValue searchKeyValue : searchFields) {
            if (searchKeyValue.getSearchField().equals(SearchFields.HIST_GUID)) {
                return true;
            }
        }
        return false;
    }

    private SearchKeyValue getDateSearchField(List<SearchKeyValue> searchFields) {
        for (SearchKeyValue searchKeyValue : searchFields) {
            if (searchKeyValue.getSearchField().equals(SearchFields.DATE)) {
                return searchKeyValue;
            }
        }
        return null;
    }

    private boolean useLike(SearchKeyValue entry) {
        for (String searchValue : entry.getSearchValues()) {
            if (searchValue.contains("*")) {
                return true;
            }
        }
        return false;
    }

    public List<Asset> getRevisionsForAsset(UUID id) {
        AuditReader auditReader = AuditReaderFactory.get(em);
        List<Asset> resultList = new ArrayList<>();

        List<Number> revisionNumbers = auditReader.getRevisions(Asset.class, id);
        for (Number rev : revisionNumbers) {
            Asset audited = auditReader.find(Asset.class, id, rev);
            resultList.add(audited);
        }
        return resultList;
    }

    public List<Asset> getAssetListByAssetGuids(List<UUID> idList) {
        if(idList.isEmpty()){
            return new ArrayList<>();
        }
        TypedQuery<Asset> query = em.createNamedQuery(Asset.ASSET_FIND_BY_IDS, Asset.class);
        query.setParameter("idList", idList);
        return query.getResultList();
    }

    public List<MicroAsset> getMicroAssetListByAssetGuids(List<UUID> idList) {
        if(idList.isEmpty()){
            return new ArrayList<>();
        }
        TypedQuery<MicroAsset> query = em.createNamedQuery(Asset.ASSET_MICRO_ASSET_BY_LIST, MicroAsset.class);
        query.setParameter("idList", idList);
        return query.getResultList();
    }

    public Asset getAssetFromAssetId(AssetIdentifier assetId, String value) {
        Asset asset;
        switch (assetId) {
            case CFR:
                asset = getAssetByCfr(value);
                break;
            case IRCS:
                asset = getAssetByIrcs(value);
                break;
            case IMO:
                asset = getAssetByImo(value);
                break;
            case MMSI:
                asset = getAssetByMmsi(value);
                break;
            case GUID:
                asset = getAssetById(UUID.fromString(value));
                break;
            case ICCAT:
                asset = getAssetByIccat(value);
                break;
            case UVI:
                asset = getAssetByUvi(value);
                break;
            case GFCM:
                asset = getAssetByGfcm(value);
                break;
            default:
                throw new IllegalArgumentException("Could not create query. Check your code AssetIdType is invalid");
        }
        return asset;
    }

    public Asset getAssetFromAssetIdAtDate(AssetIdentifier assetId, String value, Instant date) {
        Asset asset = getAssetFromAssetId(assetId, value);
        if (asset != null) {
            return getAssetAtDate(asset, date);
        } else {
            return null;
        }
    }

    public Asset getAssetAtDate(Asset asset, Instant instant) {
        Date date = Date.from(instant);
        AuditReader auditReader = AuditReaderFactory.get(em);
        try {
            return auditReader.find(Asset.class, asset.getId(), date);
        } catch (RevisionDoesNotExistException ex) {
            return getFirstRevision(asset);
        }
    }

    public Asset getFirstRevision(Asset asset) {
        AuditReader auditReader = AuditReaderFactory.get(em);
        List<Number> revisions = auditReader.getRevisions(Asset.class, asset.getId());
        if (!revisions.isEmpty()) {
            return auditReader.find(Asset.class, asset.getId(), revisions.get(0));
        }
        return null;
    }

    public Asset getAssetRevisionForHistoryId(UUID historyId) {
        AuditReader auditReader = AuditReaderFactory.get(em);
        return (Asset) auditReader.createQuery().forRevisionsOfEntity(Asset.class, true, true)
                .add(AuditEntity.property("historyId").eq(historyId))
                .getSingleResult();
    }

    public AssetRemapMapping createAssetRemapMapping(AssetRemapMapping mapping){
         em.persist(mapping);
         return mapping;
    }

    public List<AssetRemapMapping> getAllAssetRemappings(){
        Query query = em.createQuery("from AssetRemapMapping", AssetRemapMapping.class);
        return query.getResultList();
    }

    public void deleteAssetMapping(AssetRemapMapping mapping) {
        em.remove(em.contains(mapping) ? mapping : em.merge(mapping));
    }

    public ContactInfo getContactById(UUID contactId){
        return em.find(ContactInfo.class, contactId);
    }
}
