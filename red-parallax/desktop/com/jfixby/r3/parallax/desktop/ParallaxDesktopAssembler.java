
package com.jfixby.r3.parallax.desktop;

import java.io.IOException;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.github.wrebecca.bleed.RebeccaTextureBleeder;
import com.jfixby.cmns.adopted.gdx.json.RedJson;
import com.jfixby.cmns.api.collections.Collection;
import com.jfixby.cmns.api.collections.Collections;
import com.jfixby.cmns.api.collections.List;
import com.jfixby.cmns.api.collisions.Collisions;
import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.file.FileSystemSandBox;
import com.jfixby.cmns.api.file.LocalFileSystem;
import com.jfixby.cmns.api.java.gc.GCFisher;
import com.jfixby.cmns.api.json.Json;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.net.http.Http;
import com.jfixby.cmns.api.net.http.HttpURL;
import com.jfixby.cmns.api.sys.settings.ExecutionMode;
import com.jfixby.cmns.api.sys.settings.SystemSettings;
import com.jfixby.cmns.ver.Version;
import com.jfixby.psd.unpacker.api.PSDUnpacker;
import com.jfixby.psd.unpacker.core.RedPSDUnpacker;
import com.jfixby.r3.api.EngineParams.Assets;
import com.jfixby.r3.api.EngineParams.Settings;
import com.jfixby.r3.api.RedTriplane;
import com.jfixby.r3.api.logic.BusinessLogic;
import com.jfixby.r3.api.shader.R3Shader;
import com.jfixby.r3.api.ui.UI;
import com.jfixby.r3.api.ui.UIStarter;
import com.jfixby.r3.api.ui.unit.layer.LayerUtils;
import com.jfixby.r3.collide.RedCollisionsAlgebra;
import com.jfixby.r3.engine.core.Fokker;
import com.jfixby.r3.engine.core.unit.layers.RedLayerUtils;
import com.jfixby.r3.engine.core.unit.shader.R3FokkerShader;
import com.jfixby.r3.ext.api.scene2d.Scene2D;
import com.jfixby.r3.ext.api.text.R3Text;
import com.jfixby.r3.ext.text.red.RedTriplaneText;
import com.jfixby.r3.fokker.api.FokkerEngineAssembler;
import com.jfixby.r3.fokker.api.FokkerEngineParams;
import com.jfixby.r3.fokker.api.UnitsSpawner;
import com.jfixby.r3.fokker.api.assets.FokkerTextureLoader;
import com.jfixby.r3.fokker.assets.RedFokkerTextureLoader;
import com.jfixby.r3.fokker.backend.RedUnitSpawner;
import com.jfixby.r3.parallax.core.RedParallaxCore;
import com.jfixby.r3.ui.RedUIManager;
import com.jfixby.rana.api.asset.AssetsManager;
import com.jfixby.rana.api.asset.AssetsManagerFlags;
import com.jfixby.rana.api.pkg.ResourcesGroup;
import com.jfixby.rana.api.pkg.ResourcesManager;
import com.jfixby.red.engine.core.resources.RedAssetsManager;
import com.jfixby.red.engine.scene2d.RedScene2D;
import com.jfixby.red.filesystem.sandbox.RedFileSystemSandBox;
import com.jfixby.red.filesystem.virtual.InMemoryFileSystem;
import com.jfixby.red.triplane.resources.fsbased.RedResourcesManager;
import com.jfixby.redreporter.analytics.RedAnalyticsReporter;
import com.jfixby.redreporter.api.analytics.AnalyticsReporter;
import com.jfixby.redreporter.api.crash.CrashReporter;
import com.jfixby.redreporter.api.transport.ReporterTransport;
import com.jfixby.redreporter.client.http.ReporterHttpClient;
import com.jfixby.redreporter.client.http.ReporterHttpClientConfig;
import com.jfixby.redreporter.crash.RedCrashReporter;
import com.jfixby.texture.slicer.api.TextureSlicer;
import com.jfixby.texture.slicer.red.RedTextureSlicer;
import com.jfixby.tools.bleed.api.TextureBleed;
import com.jfixby.tools.gdx.texturepacker.GdxTexturePacker;
import com.jfixby.tools.gdx.texturepacker.api.TexturePacker;

public class ParallaxDesktopAssembler implements FokkerEngineAssembler {

	private static final String INSTALLATION_ID_FILE_NAME = "com.red-triplane.redparallax.iid";

	@Override
	public void assembleEngine () {

		{
			PSDUnpacker.installComponent(new RedPSDUnpacker());
			TexturePacker.installComponent(new GdxTexturePacker());
			TextureSlicer.installComponent(new RedTextureSlicer());
			Json.installComponent(new RedJson());
			TextureBleed.installComponent(new RebeccaTextureBleeder());
		}

		SystemSettings.setExecutionMode(ExecutionMode.EARLY_DEVELOPMENT);
		SystemSettings.setFlag(Settings.PrintLogMessageOnMissingSprite, true);
		SystemSettings.setFlag(Settings.ExitOnMissingSprite, false);
		SystemSettings.setFlag(Settings.AllowMissingRaster, true);
		SystemSettings.setFlag(AssetsManager.UseAssetSandBox, false);
		SystemSettings.setFlag(AssetsManager.ReportUnusedAssets, false);
		SystemSettings.setFlag(AssetsManagerFlags.AutoresolveDependencies, true);
		SystemSettings.setFlag(R3Text.RenderRasterStrings, true);
		SystemSettings.setStringParameter(FokkerEngineParams.TextureFilter.Mag, TextureFilter.Nearest + "");
		SystemSettings.setStringParameter(FokkerEngineParams.TextureFilter.Min, TextureFilter.Nearest + "");
		SystemSettings.setStringParameter(Assets.DefaultFont, "Arial");
		SystemSettings.setLongParameter(Assets.DEFAULT_LOGO_FADE_TIME, 2000L);
		SystemSettings.setStringParameter(Assets.CLEAR_SCREEN_COLOR_ARGB, "#FFeeeeee");
		SystemSettings.setLongParameter(GCFisher.DefaultBaitSize, 1 * 1024 * 1024);

		SystemSettings.setStringParameter(Version.Tags.PackageName, ParallaxVersion.packageName);
		SystemSettings.setStringParameter(Version.Tags.VersionCode, ParallaxVersion.versionCode + "");
		SystemSettings.setStringParameter(Version.Tags.VersionName, ParallaxVersion.versionName);

		deployAnalytics();

		try {
			this.installResources();
		} catch (final IOException e) {
			e.printStackTrace();
		}

		Scene2D.installComponent(new RedScene2D());
		R3Text.installComponent(new RedTriplaneText());
		R3Shader.installComponent(new R3FokkerShader());

		// FileSystemPacker.installComponent(new RedFileSystemPacker());

		FileSystemSandBox.installComponent(new RedFileSystemSandBox());

		// String java_path_cache = "D:\\[DATA]\\[RED-ASSETS]\\cache";
		// File cache_path = LocalFileSystem.newFile(java_path_cache);

		// VirtualFileSystem vfs = new VirtualFileSystem();
		// cache_path = vfs;
		LayerUtils.installComponent(new RedLayerUtils());

		FokkerTextureLoader.installComponent(new RedFokkerTextureLoader());
		FokkerTextureLoader.register();

		AssetsManager.installComponent(new RedAssetsManager());
		ResourcesManager.registerPackageReader(R3Text.getTTFFontPackageReader());
		ResourcesManager.registerPackageReader(Scene2D.getPackageReader());
		ResourcesManager.registerPackageReader(R3Text.getStringsPackageReader());
		ResourcesManager.registerPackageReader(R3Text.getTextPackageReader());
		ResourcesManager.registerPackageReader(R3Shader.getPackageReader());

		final RedUIManager tinto_ui_starter = new RedUIManager();
		UIStarter.installComponent(tinto_ui_starter);
		UI.installComponent(tinto_ui_starter);
		BusinessLogic.installComponent(new RedParallaxCore());

		Collisions.installComponent(new RedCollisionsAlgebra());
		RedTriplane.installComponent(new Fokker());

		UnitsSpawner.installComponent(new RedUnitSpawner());

	}

	static public void deployAnalytics () {
		{
			final File home = LocalFileSystem.ApplicationHome();
			final File logs = setupLogFolder(home);

			final ReporterHttpClientConfig transport_config = new ReporterHttpClientConfig();

			transport_config.setInstallationIDStorageFolder(home);
			transport_config.setIIDFileName(INSTALLATION_ID_FILE_NAME);
			transport_config.setCacheFolder(logs);
			{
				final String url_string = "https://rr-0.red-triplane.com/";
				final HttpURL url = Http.newURL(url_string);
				transport_config.addAnalyticsServerUrl(url);
			}
			{
				final String url_string = "https://rr-1.red-triplane.com/";
				final HttpURL url = Http.newURL(url_string);
				transport_config.addAnalyticsServerUrl(url);
			}
			{
				final String url_string = "https://rr-2.red-triplane.com/";
				final HttpURL url = Http.newURL(url_string);
				transport_config.addAnalyticsServerUrl(url);
			}
			final ReporterTransport transport = new ReporterHttpClient(transport_config);
			{
				CrashReporter.installComponent(new RedCrashReporter(transport));
				CrashReporter.enableErrorsListener();
				CrashReporter.enableLogsListener();
				CrashReporter.enableUncaughtExceptionHandler();
			}
			{
				AnalyticsReporter.installComponent(new RedAnalyticsReporter(transport));
			}
		}
	}

	final private static File setupLogFolder (final File home) {
		File logs = null;
		try {
			logs = home.child("logs");
			logs.makeFolder();
			if (logs.isFolder()) {
				return logs;
			}
		} catch (final IOException e) {
			L.e(e);
		}
		final InMemoryFileSystem imfs = new InMemoryFileSystem();
		return imfs.ROOT();
	}

	private void installResources () throws IOException {

		final RedResourcesManager res_manager = new RedResourcesManager();
		ResourcesManager.installComponent(res_manager);

		final File home = LocalFileSystem.ApplicationHome();
		final File assets_folder = home.child("assets");

		if (assets_folder.exists() && assets_folder.isFolder()) {
			final Collection<ResourcesGroup> locals = res_manager.findAndInstallResources(assets_folder);
			locals.print("locals");
			for (final ResourcesGroup local : locals) {
				local.rebuildAllIndexes(null);
			}

		}

		final File assets_cache_folder = home.child("assets-cache");
		{
			final List<String> tanks = Collections.newList("tank-0");
			final HttpURL bankURL = Http.newURL("https://s3.eu-central-1.amazonaws.com/com.red-triplane.assets/bank-r3");
			final ResourcesGroup bank = res_manager.installRemoteBank(bankURL, assets_cache_folder, tanks);
			bank.rebuildAllIndexes(null);
		}
	}

}
