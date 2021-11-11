package org.openmrs.module.afyastat.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dmg.pmml.FieldName;
import org.jpmml.evaluator.Computable;
import org.jpmml.evaluator.Evaluator;
import org.jpmml.evaluator.FieldValue;
import org.jpmml.evaluator.InputField;
import org.jpmml.evaluator.LoadingModelEvaluatorBuilder;
import org.jpmml.evaluator.TargetField;
import org.openmrs.module.afyastat.domain.ModelInputFields;
import org.openmrs.module.afyastat.domain.ScoringResult;
import org.openmrs.module.afyastat.exception.ScoringException;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ModelService {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	public ScoringResult score(String modelId, ModelInputFields inputFields) {
		
		try {
			InputStream stream = ModelService.class.getClassLoader().getResourceAsStream("hts_xgb_notimeplace.pmml");
			
			// Building a model evaluator from a PMML file
			Evaluator evaluator = new LoadingModelEvaluatorBuilder().load(stream).build();
			evaluator.verify();
			ScoringResult scoringResult = new ScoringResult(score(evaluator, inputFields));
			log.info("Model uploaded with model id: [{}]. Result is [{}]" + scoringResult.getResult());
			System.out.println("Model uploaded with model id: [{}]. Result is [{}]" + scoringResult.getResult());
			
			return scoringResult;
		}
		catch (Exception e) {
			e.printStackTrace();
			log.error("Exception during preparation of input parameters or scoring of values for HTS model. "
			        + e.getMessage());
			throw new ScoringException("Exception during preparation of input parameters or scoring of values", e);
		}
	}
	
	/**
	 * A method that scores a model
	 * 
	 * @param evaluator
	 * @param inputFields
	 * @return
	 */
	private Map<String, Object> score(Evaluator evaluator, ModelInputFields inputFields) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		Map<FieldName, ?> evaluationResultFromEvaluator = evaluator.evaluate(prepareEvaluationArgs(evaluator, inputFields));
		
		List<TargetField> targetFields = evaluator.getTargetFields();
		
		for (TargetField targetField : targetFields) {
			FieldName targetFieldName = targetField.getName();
			Object targetFieldValue = evaluationResultFromEvaluator.get(targetField.getName());
			
			if (targetFieldValue instanceof Computable) {
				targetFieldValue = ((Computable) targetFieldValue).getResult();
			}
			
			result.put(targetFieldName.getValue(), targetFieldValue);
		}
		return result;
	}
	
	private Map<FieldName, FieldValue> prepareEvaluationArgs(Evaluator evaluator, ModelInputFields inputFields) {
		Map<FieldName, FieldValue> arguments = new LinkedHashMap<FieldName, FieldValue>();
		
		List<InputField> evaluatorFields = evaluator.getActiveFields();
		
		for (InputField evaluatorField : evaluatorFields) {
			FieldName evaluatorFieldName = evaluatorField.getName();
			String evaluatorFieldNameValue = evaluatorFieldName.getValue();
			
			Object inputValue = inputFields.getFields().get(evaluatorFieldNameValue);
			
			if (inputValue == null) {
				//Logger.warn("Model value not found for the following field [{}]", evaluatorFieldNameValue);
			}
			
			arguments.put(evaluatorFieldName, evaluatorField.prepare(inputValue));
		}
		return arguments;
	}
}
