package eu.europa.ec.fisheries.uvms.rest.asset.service;

import java.util.Set;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.bind.adapter.JsonbAdapter;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilter;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterList;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterQuery;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterValue;

public class AssetFilterListRestResourceAdapter implements JsonbAdapter<AssetFilterList, JsonObject>{

	@Override
	public JsonObject adaptToJson(AssetFilterList assetFilterList) throws Exception {
	
		JsonObjectBuilder jsonObjectOfFilters = Json.createObjectBuilder();
		for(AssetFilter assetFilter : assetFilterList.getAssetFilterList()) {
			Set<AssetFilterQuery>  assetFilterQuerySet = assetFilter.getQueries();
		
	    	for(AssetFilterQuery assetFilterQuery : assetFilterQuerySet){ 
	        	JsonArrayBuilder jsonValueArray = Json.createArrayBuilder();
	    		Set<AssetFilterValue> assetFilterValues = assetFilterQuery.getValues();
	    		
	    		for(AssetFilterValue assetFilterValue : assetFilterValues) {
	    			if( assetFilterQuery.getIsNumber() == false) {
	    				jsonValueArray
	    					.add(assetFilterValue.getValueString());
	        		}
	    			else {
	    				JsonObject jsonValueObject = Json.createObjectBuilder()
	        				.add("operator", assetFilterValue.getOperator())
	        				.add("value", assetFilterValue.getValueNumber().doubleValue())
	        				.build();
	    				jsonValueArray
	    					.add(jsonValueObject);
	        		}
	    		}
	    		JsonObject jsonQueryBuilder =  Json.createObjectBuilder()
	    			.add("inverse", assetFilterQuery.getInverse())
	    			.add("isNumber", assetFilterQuery.getIsNumber())
	    			.add("type", assetFilterQuery.getType())
	    			.add("values",jsonValueArray.build())
	    			.build();
	    		JsonObject jsonFilter = Json.createObjectBuilder()
	    			.add("id", assetFilter.getId().toString())
	    			.add("name", assetFilter.getName())
	    			.add("filter", jsonQueryBuilder)
	    			.build();
	    		jsonObjectOfFilters.add(assetFilter.getId().toString(), jsonFilter);
	    	}
		}
		return Json.createObjectBuilder()
				.add("savedFilters", jsonObjectOfFilters.build())
				.build();
	}

	@Override
	public AssetFilterList adaptFromJson(JsonObject adapted) throws Exception {
		return null;
	}
}

	