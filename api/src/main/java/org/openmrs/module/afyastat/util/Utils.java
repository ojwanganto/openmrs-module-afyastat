package org.openmrs.module.afyastat.util;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.afyastat.api.service.InfoService;
import org.openmrs.module.afyastat.domain.ModelInputFields;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Utils {
	
	/**
	 * Lifted from KenyaEMR common metadata
	 */
	public static final String NEXT_OF_KIN_ADDRESS = "7cf22bec-d90a-46ad-9f48-035952261294";
	
	public static final String NEXT_OF_KIN_CONTACT = "342a1d39-c541-4b29-8818-930916f4c2dc";
	
	public static final String NEXT_OF_KIN_NAME = "830bef6d-b01f-449d-9f8d-ac0fede8dbd3";
	
	public static final String NEXT_OF_KIN_RELATIONSHIP = "d0aa9fd1-2ac5-45d8-9c5e-4317c622c8f5";
	
	public static final String SUBCHIEF_NAME = "40fa0c9c-7415-43ff-a4eb-c7c73d7b1a7a";
	
	public static final String TELEPHONE_CONTACT = "b2c38640-2603-4629-aebd-3b54f33f1e3a";
	
	public static final String EMAIL_ADDRESS = "b8d0b331-1d2d-4a9a-b741-1816f498bdb6";
	
	public static final String ALTERNATE_PHONE_CONTACT = "94614350-84c8-41e0-ac29-86bc107069be";
	
	public static final String NEAREST_HEALTH_CENTER = "27573398-4651-4ce5-89d8-abec5998165c";
	
	public static final String GUARDIAN_FIRST_NAME = "8caf6d06-9070-49a5-b715-98b45e5d427b";
	
	public static final String GUARDIAN_LAST_NAME = "0803abbd-2be4-4091-80b3-80c6940303df";
	
	public static final String CWC_NUMBER = "1dc8b419-35f2-4316-8d68-135f0689859b";
	
	public static final String DISTRICT_REGISTRATION_NUMBER = "d8ee3b8c-a8fc-4d6b-af6a-9423be5f8906";
	
	public static final String HEI_UNIQUE_NUMBER = "0691f522-dd67-4eeb-92c8-af5083baf338";
	
	public static final String OPENMRS_ID = "dfacd928-0370-4315-99d7-6ec1c9f7ae76";
	
	public static final String NATIONAL_ID = "49af6cdc-7968-4abb-bf46-de10d7f4859f";
	
	public static final String OLD = "8d79403a-c2cc-11de-8d13-0010c6dffd0f";
	
	public static final String PATIENT_CLINIC_NUMBER = "b4d66522-11fc-45c7-83e3-39a1af21ae0d";
	
	public static final String UNIQUE_PATIENT_NUMBER = "05ee9cf4-7242-4a17-b4d4-00f707265c8a";
	
	public static final String NATIONAL_UNIQUE_PATIENT_IDENTIFIER = "f85081e2-b4be-4e48-b3a4-7994b69bb101";
	
	public static final String HTS = "9c0a7a57-62ff-4f75-babe-5835b0e921b7";
	
	public static final String HTS_INITIAL_TEST = "402dc5d7-46da-42d4-b2be-f43ea4ad87b0";
	
	public static final String HTS_CONFIRMATORY_TEST = "b08471f6-0892-4bf7-ab2b-bf79797b8ea4";
	
	public static String fetchRequestBody(BufferedReader reader) {
		String requestBodyJsonStr = "";
		try {
			
			BufferedReader br = new BufferedReader(reader);
			String output = "";
			while ((output = reader.readLine()) != null) {
				requestBodyJsonStr += output;
			}
			
		}
		catch (IOException e) {
			
			System.out.println("IOException: " + e.getMessage());
			
		}
		return requestBodyJsonStr;
	}
	
	/**
	 * Finds the last encounter during the program enrollment with the given encounter type
	 * 
	 * @param type the encounter type
	 * @return the encounter
	 */
	public static Encounter lastEncounter(Patient patient, EncounterType type, List<Form> forms) {
		List<Encounter> encounters = Context.getEncounterService().getEncounters(patient, null, null, null, forms,
		    Collections.singleton(type), null, null, null, false);
		return encounters.size() > 0 ? encounters.get(encounters.size() - 1) : null;
	}
	
	/**
	 * Check in queue, error, and archive data for the uuid
	 * 
	 * @param uuid
	 * @return
	 */
	public static boolean afyastatFormAlreadyExists(String uuid) {
		InfoService infoService = Context.getService(InfoService.class);
		
		if (StringUtils.isBlank(uuid)) {
			return false;
		}
		
		if (infoService.getQueueDataByUuid(uuid) != null) {
			return true;
		}
		
		if (infoService.getArchiveDataByUuid(uuid) != null) {
			return true;
		}
		
		if (infoService.getErrorDataByUuid(uuid) != null) {
			return true;
		}
		return false;
	}
	
	/**
	 * Extracts model variables from request body Variable values are of float type
	 * 
	 * @param requestBodyString
	 * @return
	 */
	public static ModelInputFields extractHTSCaseFindingVariablesFromRequestBody(String requestBodyString) {
		
		System.out.println("Request body: " + requestBodyString);
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode tree = null;
		Map<String, Object> modelParams = new HashMap<String, Object>();
		try {
			tree = (ObjectNode) mapper.readTree(requestBodyString);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		Iterator<Map.Entry<String, JsonNode>> it = tree.getFields();
		while (it.hasNext()) {
			Map.Entry<String, JsonNode> field = it.next();
			String keyId = field.getKey();
			keyId = keyId.replace("_", "."); // match the variable names are expected in the model
			int keyValue = field.getValue().getIntValue();
			modelParams.put(keyId, keyValue);
			System.out.println("Param Key: " + keyId + ", value: " + keyValue);
		}
		
		ModelInputFields inputFields = new ModelInputFields();
		inputFields.setFields(modelParams);
		return inputFields;
	}
}
