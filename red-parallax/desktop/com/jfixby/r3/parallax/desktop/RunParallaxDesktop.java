
package com.jfixby.r3.parallax.desktop;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.FokkerLwjglApplication;
import com.badlogic.gdx.backends.lwjgl.FokkerLwjglApplicationConfiguration;
import com.jfixby.cmns.adopted.gdx.GdxSimpleTriangulator;
import com.jfixby.cmns.adopted.gdx.base64.GdxBase64;
import com.jfixby.cmns.adopted.gdx.json.RedJson;
import com.jfixby.cmns.api.angles.Angles;
import com.jfixby.cmns.api.assets.Names;
import com.jfixby.cmns.api.base64.Base64;
import com.jfixby.cmns.api.collections.Collections;
import com.jfixby.cmns.api.color.Colors;
import com.jfixby.cmns.api.debug.Debug;
import com.jfixby.cmns.api.err.Err;
import com.jfixby.cmns.api.geometry.Geometry;
import com.jfixby.cmns.api.graphs.Graphs;
import com.jfixby.cmns.api.input.UserInput;
import com.jfixby.cmns.api.io.IO;
import com.jfixby.cmns.api.json.Json;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.math.FloatMath;
import com.jfixby.cmns.api.math.IntegerMath;
import com.jfixby.cmns.api.math.MathTools;
import com.jfixby.cmns.api.math.SimpleTriangulator;
import com.jfixby.cmns.api.md5.MD5;
import com.jfixby.cmns.api.sys.Sys;
import com.jfixby.cmns.api.sys.settings.SystemSettings;
import com.jfixby.cmns.api.taskman.TaskManager;
import com.jfixby.cmns.api.util.JUtils;
import com.jfixby.r3.fokker.api.FokkerEngineAssembler;
import com.jfixby.r3.fokker.api.UnitsMachineExecutor;
import com.jfixby.red.color.RedColors;
import com.jfixby.red.debug.RedDebug;
import com.jfixby.red.desktop.collections.DesktopCollections;
import com.jfixby.red.desktop.log.DesktopLogger;
import com.jfixby.red.desktop.math.DesktopFloatMath;
import com.jfixby.red.desktop.sys.DesktopSystem;
import com.jfixby.red.engine.core.FokkerStarter;
import com.jfixby.red.engine.core.FokkerStarterConfig;
import com.jfixby.red.err.RedError;
import com.jfixby.red.geometry.RedGeometry;
import com.jfixby.red.graphs.RedGraphs;
import com.jfixby.red.input.RedInput;
import com.jfixby.red.io.RedIO;
import com.jfixby.red.math.RedAngles;
import com.jfixby.red.math.RedIntegerMath;
import com.jfixby.red.math.RedMathTools;
import com.jfixby.red.name.RedAssetsNamespace;
import com.jfixby.red.sys.RedSystemSettings;
import com.jfixby.red.sys.RedTaskManager;
import com.jfixby.red.util.RedJUtils;
import com.jfixby.red.util.md5.RSADataSecurityIncMD5;
import com.jfixby.redtriplane.fokker.adaptor.GdxAdaptor;

public class RunParallaxDesktop {
	public static void main (final String[] arg) {

		setupBasicComponents();
		final FokkerStarterConfig config = FokkerStarter.newRedTriplaneConfig();

		final FokkerEngineAssembler engine_assembler = new ParallaxDesktopAssembler();
		config.setEngineAssembler(engine_assembler);

		final FokkerStarter triplane_starter = FokkerStarter.newRedTriplane(config);
		final UnitsMachineExecutor machine = triplane_starter.getUnitsMachineExecutor();

		final GdxAdaptor adaptor = new GdxAdaptor(machine);

		final FokkerLwjglApplicationConfiguration cfg = new FokkerLwjglApplicationConfiguration();
		cfg.title = "Test";
		cfg.useGL30 = false;
		cfg.width = 1024;
		cfg.height = 768;
// cfg.vSyncEnabled = false;
// cfg.r = 1;
// cfg.g = 1;
// cfg.b = 1;
// cfg.a = 1;
// cfg.overrideDensity = 10;
// cfg.foregroundFPS = 60;

		final ApplicationListener gdx_listener = adaptor.getGDXApplicationListener();

		// gdx_listener = new HttpRequestTest();
		// GdxEntryPoint point = new GdxEntryPoint();
		// new LwjglApplication(point, cfg);
		new FokkerLwjglApplication(gdx_listener, cfg);

	}

	private static void setupBasicComponents () {
		L.installComponent(new DesktopLogger());
		Collections.installComponent(new DesktopCollections());
		Err.installComponent(new RedError());
		Debug.installComponent(new RedDebug());
		JUtils.installComponent(new RedJUtils());
		FloatMath.installComponent(new DesktopFloatMath());
		TaskManager.installComponent(new RedTaskManager());
		Sys.installComponent(new DesktopSystem());
		SystemSettings.installComponent(new RedSystemSettings());

		IntegerMath.installComponent(new RedIntegerMath());
		Names.installComponent(new RedAssetsNamespace());
		IO.installComponent(new RedIO());
		Graphs.installComponent(new RedGraphs());
		SimpleTriangulator.installComponent(new GdxSimpleTriangulator());
		Angles.installComponent(new RedAngles());

		UserInput.installComponent(new RedInput());

		final RedGeometry geometry = new RedGeometry();
		Geometry.installComponent(geometry);
		Colors.installComponent(new RedColors());
		MathTools.installComponent(new RedMathTools());
		// --
		Json.installComponent(new RedJson());
		Base64.installComponent(new GdxBase64());
		MD5.installComponent(new RSADataSecurityIncMD5());
	}
}
