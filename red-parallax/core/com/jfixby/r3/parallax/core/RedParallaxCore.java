
package com.jfixby.r3.parallax.core;

import com.jfixby.cmns.api.assets.AssetID;
import com.jfixby.cmns.api.assets.Names;
import com.jfixby.r3.api.logic.BusinessLogicComponent;
import com.jfixby.r3.api.ui.UI;

public class RedParallaxCore implements BusinessLogicComponent {

	public static final AssetID unit_id = Names.newAssetID("com.jfixby.r3.parallax.ui.Ui");

	@Override
	public void start () {

		UI.loadUnit(unit_id);

	}
}
