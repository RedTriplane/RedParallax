
package com.jfixby.r3.parallax.ui;

import com.jfixby.cmns.api.assets.AssetID;
import com.jfixby.cmns.api.assets.Names;
import com.jfixby.cmns.api.floatn.Float2;
import com.jfixby.cmns.api.geometry.Geometry;
import com.jfixby.cmns.api.input.Key;
import com.jfixby.r3.api.ui.unit.ComponentsFactory;
import com.jfixby.r3.api.ui.unit.RootLayer;
import com.jfixby.r3.api.ui.unit.Unit;
import com.jfixby.r3.api.ui.unit.UnitManager;
import com.jfixby.r3.api.ui.unit.input.MouseMovedEvent;
import com.jfixby.r3.api.ui.unit.input.MouseScrolledEvent;
import com.jfixby.r3.api.ui.unit.input.TouchDownEvent;
import com.jfixby.r3.api.ui.unit.input.TouchDraggedEvent;
import com.jfixby.r3.api.ui.unit.input.TouchUpEvent;
import com.jfixby.r3.api.ui.unit.parallax.Parallax;
import com.jfixby.r3.api.ui.unit.update.UnitClocks;
import com.jfixby.r3.api.ui.unit.user.KeyboardInputEventListener;
import com.jfixby.r3.api.ui.unit.user.MouseInputEventListener;
import com.jfixby.r3.api.ui.unit.user.UpdateListener;
import com.jfixby.r3.ext.api.scene2d.Scene;
import com.jfixby.r3.ext.api.scene2d.Scene2D;
import com.jfixby.r3.ext.api.scene2d.Scene2DSpawningConfig;
import com.jfixby.rana.api.asset.AssetHandler;
import com.jfixby.rana.api.asset.AssetsConsumer;
import com.jfixby.rana.api.asset.AssetsManager;

public class ParallaxUI implements Unit, AssetsConsumer {

	private RootLayer root;
	private ComponentsFactory factory;
	private final AssetID scene_id = Names.newAssetID("com.jfixby.r3.parallax.ui.scene.psd");

	long timestamp = 0;
	private Scene game_scene;
	private AssetHandler assetHandler;
	private Parallax parallax;

	@Override
	public void onCreate (final UnitManager unitManager) {
		this.root = unitManager.getRootLayer();
		this.factory = unitManager.getComponentsFactory();

		this.root.attachComponent(this.onUpdate);
		this.root.attachComponent(this.onMouseInput);
		this.deployScene();
	}

	private void deployScene () {

		final Scene2DSpawningConfig config = Scene2D.newSceneSpawningConfig();
		config.setStructureID(this.scene_id);
		this.game_scene = Scene2D.spawnScene(this.factory, config);
		this.parallax = this.game_scene.listParallaxes().getLast();
		this.assetHandler = AssetsManager.obtainAsset(this.scene_id, this);
		this.root.attachComponent(this.game_scene);
		final long timestamp = this.assetHandler.readPackageTimeStamp();
	}

	final UpdateListener onUpdate = new UpdateListener() {
		@Override
		public void onUpdate (final UnitClocks unit_clock) {

		}
	};

	final KeyboardInputEventListener onKeyboardInput = new KeyboardInputEventListener() {

		@Override
		public boolean onKeyDown (final Key key) {
			return false;
		}

		@Override
		public boolean onKeyUp (final Key key) {
			return false;
		}

		@Override
		public boolean onCharTyped (final char char_typed) {
			return false;
		}

		@Override
		public boolean onMouseScrolled (final MouseScrolledEvent event) {
			return false;
		}

	};

	boolean mouse_pressed = false;
	final Float2 mouseStart = Geometry.newFloat2();
	final Float2 mouseCurrent = Geometry.newFloat2();
	final Float2 mouseDelta = Geometry.newFloat2();
	final Float2 globalDelta = Geometry.newFloat2();
	final Float2 tmp = Geometry.newFloat2();

	final MouseInputEventListener onMouseInput = new MouseInputEventListener() {

		@Override
		public boolean onMouseMoved (final MouseMovedEvent input_event) {
// L.d(input_event);
			ParallaxUI.this.mouse_pressed = false;
			return true;
		}

		@Override
		public boolean onTouchDown (final TouchDownEvent input_event) {
// L.d(input_event);
			ParallaxUI.this.mouse_pressed = true;
			ParallaxUI.this.mouseStart.set(input_event.getCanvasPosition());
			ParallaxUI.this.mouseCurrent.set(input_event.getCanvasPosition());
			return true;
		}

		@Override
		public boolean onTouchUp (final TouchUpEvent input_event) {
// L.d(input_event);
			ParallaxUI.this.mouseCurrent.set(input_event.getCanvasPosition());
			ParallaxUI.this.mouse_pressed = !true;
			ParallaxUI.this.updateMouseDelta();
			ParallaxUI.this.globalDelta.setLinearSum(ParallaxUI.this.globalDelta, 1, ParallaxUI.this.mouseDelta, 1);
			return true;
		}

		@Override
		public boolean onTouchDragged (final TouchDraggedEvent input_event) {
// L.d(input_event);
			ParallaxUI.this.mouseCurrent.set(input_event.getCanvasPosition());
			ParallaxUI.this.updateMouseDelta();
			ParallaxUI.this.mouse_pressed = true;
			return true;
		}

	};

	private void updateMouseDelta () {
		this.mouseDelta.setLinearSum(this.mouseCurrent, 1, this.mouseStart, -1);
		this.tmp.setLinearSum(this.globalDelta, 1, this.mouseDelta, 1);
		this.parallax.setParallaxOffset(this.tmp);

	}

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
