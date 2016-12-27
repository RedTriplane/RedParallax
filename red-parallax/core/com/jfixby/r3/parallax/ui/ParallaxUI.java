
package com.jfixby.r3.parallax.ui;

import java.io.IOException;

import com.jfixby.r3.api.ui.UI;
import com.jfixby.r3.api.ui.unit.ComponentsFactory;
import com.jfixby.r3.api.ui.unit.RootLayer;
import com.jfixby.r3.api.ui.unit.Unit;
import com.jfixby.r3.api.ui.unit.UnitManager;
import com.jfixby.r3.api.ui.unit.input.MouseScrolledEvent;
import com.jfixby.r3.api.ui.unit.parallax.Parallax;
import com.jfixby.r3.api.ui.unit.raster.GraphicalConsole;
import com.jfixby.r3.api.ui.unit.update.UnitClocks;
import com.jfixby.r3.api.ui.unit.user.KeyboardInputEventListener;
import com.jfixby.r3.api.ui.unit.user.UpdateListener;
import com.jfixby.r3.ext.api.scene2d.Scene;
import com.jfixby.r3.ext.api.scene2d.Scene2D;
import com.jfixby.r3.ext.api.scene2d.Scene2DSpawningConfig;
import com.jfixby.r3.parallax.pack.PackConfig;
import com.jfixby.r3.parallax.pack.RepackParallaxScene;
import com.jfixby.rana.api.asset.AssetsConsumer;
import com.jfixby.rana.api.asset.AssetsManager;
import com.jfixby.rana.api.pkg.PackageReaderListener;
import com.jfixby.rana.api.pkg.ResourceRebuildIndexListener;
import com.jfixby.rana.api.pkg.ResourcesGroup;
import com.jfixby.rana.api.pkg.ResourcesManager;
import com.jfixby.scarabei.api.assets.ID;
import com.jfixby.scarabei.api.assets.Names;
import com.jfixby.scarabei.api.file.File;
import com.jfixby.scarabei.api.file.LocalFileSystem;
import com.jfixby.scarabei.api.floatn.Float2;
import com.jfixby.scarabei.api.geometry.Geometry;
import com.jfixby.scarabei.api.input.Key;
import com.jfixby.scarabei.api.input.UserInput;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.api.sys.Sys;

public class ParallaxUI implements Unit, AssetsConsumer {

	private RootLayer root;
	private ComponentsFactory factory;
	public static final ID scene_id = Names.newID("com.jfixby.r3.parallax.ui.scene.psd");

	long timestamp = 0;
	private Scene game_scene;
// private AssetHandler assetHandler;
	private Parallax parallax;
	private File psdfile;
	private long psdVersion;
	private long previouspsdVersion;
	private double parallaxWidth;
	private GraphicalConsole console;
	GifRecorder recorder;
	long lastPSDCheckTimestamp = 0;
	double frame = -1;
	long DELTA = 1000;
	boolean animating = true;

	final MouseCapture mouseCap = new MouseCapture(this);
	final Float2 tmp = Geometry.newFloat2();

	@Override
	public void onCreate (final UnitManager unitManager) {
		L.d("CREATE " + this);
		this.root = unitManager.getRootLayer();
		this.factory = unitManager.getComponentsFactory();
		this.recorder = new GifRecorder(unitManager.getToolkit());
		this.root.attachComponent(this.onUpdate);

		this.root.attachComponent(this.onKeyboardInput);
		this.root.attachComponent(this.mouseCap);

		final Integer v = null;
// v.intValue();// simulate crash

		this.deployScene();
// this.root.attachComponent(this.console);
		this.psdfile = LocalFileSystem.ApplicationHome().child("input-psd").child("scene.psd");
		try {
			this.psdVersion = this.psdfile.lastModified();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		this.previouspsdVersion = this.psdVersion;
	}

	private void deployScene () {

		final Scene2DSpawningConfig config = Scene2D.newSceneSpawningConfig();
		config.setStructureID(this.scene_id);
		config.setPackageListener(PackageReaderListener.DEFAULT);

		this.game_scene = Scene2D.spawnScene(this.factory, config);
		this.parallax = this.game_scene.listParallaxes().getLast();

		this.root.attachComponent(this.game_scene);
		this.parallax.setPositionX(0);
		this.parallax.setPositionY(0);
		this.parallaxWidth = this.parallax.getWidth();
	}

	final UpdateListener onUpdate = new UpdateListener() {
		@Override
		public void onUpdate (final UnitClocks unit_clock) {

			if (ParallaxUI.this.animating) {
				ParallaxUI.this.frame++;
				ParallaxUI.this.tmp.setXY();
				ParallaxUI.this.tmp.setX(Math.sin(ParallaxUI.this.frame / 60d));
				ParallaxUI.this.tmp.add(1, 0);
				ParallaxUI.this.tmp.scaleXY(0.5d);
				ParallaxUI.this.setParallax(ParallaxUI.this.tmp);
				if (ParallaxUI.this.frame % 2 == 0) {
					ParallaxUI.this.recorder.push();
				}
			}
			final long current = Sys.SystemTime().currentTimeMillis();
			if ((current - ParallaxUI.this.lastPSDCheckTimestamp) <= ParallaxUI.this.DELTA) {
				return;
			}
			try {
				ParallaxUI.this.psdVersion = ParallaxUI.this.psdfile.lastModified();
			} catch (final IOException e) {
				e.printStackTrace();
			}
			if (ParallaxUI.this.psdVersion == ParallaxUI.this.previouspsdVersion) {
				return;
			}

			ParallaxUI.this.repack();
		}

	};
	public static final ID unit_id = Names.newID("com.jfixby.r3.parallax.ui.ParallaxUI");

	private void repack () {
		ParallaxUI.this.recorder.stop();
		try {
			RepackParallaxScene.repack();

		} catch (final IOException e) {
			e.printStackTrace();
		}
		UI.loadUnit(unit_id);

	}

	final KeyboardInputEventListener onKeyboardInput = new KeyboardInputEventListener() {

		@Override
		public boolean onKeyDown (final Key key) {
			if (UserInput.Keyboard().G() == key) {
				ParallaxUI.this.recorder.start();
			}
			return true;
		}

		@Override
		public boolean onKeyUp (final Key key) {
			if (UserInput.Keyboard().R() == key) {
				ParallaxUI.this.repack();
			}
			if (UserInput.Keyboard().G() == key) {
				ParallaxUI.this.recorder.stop();
			}
			if (UserInput.Keyboard().A() == key) {
				ParallaxUI.this.animating = !ParallaxUI.this.animating;
			}
			return true;
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

	public void setParallax (final Float2 value) {
		this.parallax.setParallaxOffset(value);
	}

	@Override
	public void onDestroy () {
		AssetsManager.purge();
		final ResourceRebuildIndexListener listener = null;
		// AssetsManager.printAllLoadedAssets();
		final ResourcesGroup group = ResourcesManager.getResourcesGroup(Names.newID(PackConfig.BANK_NAME));
		group.rebuildAllIndexes(listener);
		group.printAllIndexes();
	}

	public double getParallaxWidth () {
		return this.parallaxWidth;
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

}
