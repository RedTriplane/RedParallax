
package com.jfixby.r3.parallax.core;

import com.jfixby.r3.api.logic.GameStarter;
import com.jfixby.r3.api.ui.FokkerUI;
import com.jfixby.scarabei.api.assets.ID;
import com.jfixby.scarabei.api.assets.Names;

public class RedParallaxCore implements GameStarter {

	public static final ID unit_id = Names.newID("com.jfixby.r3.parallax.ui.ParallaxUI");

	@Override
	public void onGameStart () {

		FokkerUI.loadUnit(unit_id);

	}
}
