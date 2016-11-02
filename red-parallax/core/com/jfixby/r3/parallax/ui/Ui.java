
package com.jfixby.r3.parallax.ui;

import com.jfixby.cmns.api.assets.AssetID;
import com.jfixby.cmns.api.assets.Names;
import com.jfixby.r3.api.ui.unit.ComponentsFactory;
import com.jfixby.r3.api.ui.unit.RootLayer;
import com.jfixby.r3.api.ui.unit.Unit;
import com.jfixby.r3.api.ui.unit.UnitManager;
import com.jfixby.r3.api.ui.unit.update.OnUpdateListener;
import com.jfixby.r3.api.ui.unit.update.UnitClocks;
import com.jfixby.r3.ext.api.scene2d.Scene;
import com.jfixby.r3.ext.api.scene2d.Scene2D;
import com.jfixby.r3.ext.api.scene2d.Scene2DSpawningConfig;
import com.jfixby.rana.api.asset.AssetHandler;
import com.jfixby.rana.api.asset.AssetsConsumer;
import com.jfixby.rana.api.asset.AssetsManager;

public class Ui implements Unit, AssetsConsumer {

	private RootLayer root;
	private ComponentsFactory factory;
	private final AssetID scene_id = Names.newAssetID("com.jfixby.r3.parallax.ui.scene.psd");

	long timestamp = 0;
	private Scene game_scene;
	private AssetHandler assetHandler;

	@Override
	public void onCreate (final UnitManager unitManager) {
		this.root = unitManager.getRootLayer();
		this.factory = unitManager.getComponentsFactory();

		this.root.attachComponent(this.onUpdate);
		this.deployScene();
	}

	private void deployScene () {

		final Scene2DSpawningConfig config = Scene2D.newSceneSpawningConfig();
		config.setStructureID(this.scene_id);
		this.game_scene = Scene2D.spawnScene(this.factory, config);
		this.assetHandler = AssetsManager.obtainAsset(this.scene_id, this);
		this.root.attachComponent(this.game_scene);
		final long timestamp = this.assetHandler.readPackageTimeStamp();
	}

	final OnUpdateListener onUpdate = new OnUpdateListener() {
		@Override
		public void onUpdate (final UnitClocks unit_clock) {

		}
	};

	@Override
	public void onStart () {
	}

	@Override
	public void onResume () {
	}

	@Override
	public void onPause () {
	}

	@Override
	public void onDestroy () {
	}

}
