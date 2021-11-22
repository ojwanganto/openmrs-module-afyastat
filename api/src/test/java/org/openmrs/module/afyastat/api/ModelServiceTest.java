package org.openmrs.module.afyastat.api;

import junit.framework.TestCase;
import org.junit.Test;
import org.openmrs.module.afyastat.domain.ModelInputFields;
import org.openmrs.module.afyastat.domain.ScoringResult;
import org.openmrs.module.afyastat.util.Utils;

public class ModelServiceTest extends TestCase {
	
	ModelService modelService;
	
	public void setUp() throws Exception {
		super.setUp();
		modelService = new ModelService();
	}
	
	@Test
	public void testTest() {
		assertTrue(true);
	}
	
	@Test
	public float scoreModel() {
		String testPayload = "{\"AgeAtTest\":29,\"MonthsSinceLastTest\":8,\"Gender_Male\":1,\"KeyPopulationType_GP\":1,\"KeyPopulationType_SW\":0,\"MaritalStatus_Married\":0,\"MaritalStatus_Divorced\":0,\"MaritalStatus_Polygamous\":1,\"MaritalStatus_Widowed\":0,\"MaritalStatus_Minor\":0,\"PatientDisabled_Not_Disabled\":1,\"EverTestedForHIV_Yes\":1,\"ClientTestedAs_Individual\":1,\"EntryPoint_VCT\":1,\"EntryPoint_OPD\":0,\"EntryPoint_MTC\":0,\"EntryPoint_IPD\":0,\"EntryPoint_MOBILE\":0,\"EntryPoint_Other\":0,\"EntryPoint_HB\":0,\"EntryPoint_PEDS\":0,\"TestingStrategy_VCT\":1,\"TestingStrategy_HB\":0,\"TestingStrategy_MOBILE\":0,\"TestingStrategy_HP\":1,\"TestingStrategy_NP\":0,\"TBScreening_No_Presumed_TB\":1,\"ClientSelfTested_No\":0}";
		ModelInputFields inputFields = Utils.extractHTSCaseFindingVariablesFromRequestBody(testPayload);
		ScoringResult scoringResult = modelService.score("1", inputFields);
		return 1;
	}
}
