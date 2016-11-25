
package com.jfixby.r3.parallax.core;

import com.jfixby.cmns.api.assets.ID;
import com.jfixby.cmns.api.assets.Names;
import com.jfixby.r3.api.logic.BusinessLogicComponent;
import com.jfixby.r3.api.ui.UI;

public class RedParallaxCore implements BusinessLogicComponent {

	public static final ID unit_id = Names.newAssetID("com.jfixby.r3.parallax.ui.ParallaxUI");

	@Override
	public void start () {

		UI.loadUnit(unit_id);

	}
}
