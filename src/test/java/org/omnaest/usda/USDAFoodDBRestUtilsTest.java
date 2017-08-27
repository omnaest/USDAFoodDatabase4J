/*

	Copyright 2017 Danny Kunz

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.


*/
package org.omnaest.usda;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.omnaest.usda.domain.Nutrient;
import org.omnaest.utils.JSONHelper;

public class USDAFoodDBRestUtilsTest
{
	private static final String API_KEY = "UCmapXPH39utkRDoElGVemeISzSQmDROHyb2c9Kf";

	@Test
	@Ignore
	public void testGetNutrients() throws Exception
	{
		List<Nutrient> nutrients = USDAFoodDBRestUtils.getNutrients(API_KEY);
		System.out.println(JSONHelper.prettyPrint(nutrients));
	}

}
