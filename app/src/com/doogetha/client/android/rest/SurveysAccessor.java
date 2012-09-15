package de.letsdoo.client.android.rest;

import de.letsdoo.server.vo.SurveyVo;
import de.letsdoo.server.vo.SurveysVo;
import de.potpiejimmy.util.RestResourceAccessor;

public class SurveysAccessor extends RestResourceAccessor<SurveysVo, SurveyVo> {
	public SurveysAccessor(String url) {
		super(url, SurveysVo.class, SurveyVo.class);
	}
}
