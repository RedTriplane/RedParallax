
package com.jfixby.r3.parallax.desktop;

import java.io.IOException;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.github.wrebecca.bleed.RebeccaTextureBleeder;
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
import com.jfixby.red.triplane.resources.fsbased.RedResourcesManager;
import com.jfixby.red.triplane.resources.fsbased.RedResourcesManagerSpecs;
import com.jfixby.redreporter.analytics.RedAnalyticsReporter;
import com.jfixby.redreporter.api.analytics.AnalyticsReporter;
import com.jfixby.redreporter.api.crash.CrashReporter;
import com.jfixby.redreporter.api.transport.ReporterTransport;
import com.jfixby.redreporter.client.http.ReporterHttpClient;
import com.jfixby.redreporter.client.http.ReporterHttpClientConfig;
import com.jfixby.redreporter.crash.RedCrashReporter;
import com.jfixby.scarabei.adopted.gdx.json.GoogleGson;
import com.jfixby.scarabei.api.collections.Collection;
import com.jfixby.scarabei.api.collections.Collections;
import com.jfixby.scarabei.api.collections.List;
import com.jfixby.scarabei.api.collisions.Collisions;
import com.jfixby.scarabei.api.desktop.ImageAWT;
import com.jfixby.scarabei.api.file.File;
import com.jfixby.scarabei.api.file.FileSystemSandBox;
import com.jfixby.scarabei.api.file.LocalFileSystem;
import com.jfixby.scarabei.api.java.gc.GCFisher;
import com.jfixby.scarabei.api.json.Json;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.api.net.http.Http;
import com.jfixby.scarabei.api.net.http.HttpURL;
import com.jfixby.scarabei.api.sys.settings.ExecutionMode;
import com.jfixby.scarabei.api.sys.settings.SystemSettings;
import com.jfixby.scarabei.api.taskman.TASK_TYPE;
import com.jfixby.scarabei.api.ver.Version;
import com.jfixby.scarabei.red.desktop.image.RedImageAWT;
import com.jfixby.scarabei.red.filesystem.sandbox.RedFileSystemSandBox;
import com.jfixby.scarabei.red.filesystem.virtual.InMemoryFileSystem;
import com.jfixby.texture.slicer.api.TextureSlicer;
import com.jfixby.texture.slicer.red.RedTextureSlicer;
import com.jfixby.tools.bleed.api.TextureBleed;
import com.jfixby.tools.gdx.texturepacker.GdxTexturePacker;
import com.jfixby.tools.gdx.texturepacker.api.TexturePacker;

public class ParallaxDesktopAssembler implements FokkerEngineAssembler {

	private static final String INSTALLATION_ID_FILE_NAME = "com.red-triplane.redparallax.iid";

	static public void deployAnalytics () {
		{
			final File home = LocalFileSystem.ApplicationHome();
			final File logs = ParallaxDesktopAssembler.setupLogFolder(home);

			final ReporterHttpClientConfig transport_config = new ReporterHttpClientConfig();

			transport_config.setInstallationIDStorageFolder(home);
			transport_config.setIIDFileName(ParallaxDesktopAssembler.INSTALLATION_ID_FILE_NAME);
			transport_config.setCacheFolder(logs);
			transport_config.setTaskType(TASK_TYPE.SEPARATED_THREAD);
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
				AnalyticsReporter.reportStart();
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

	@Override
	public void assembleEngine () {

		{
			PSDUnpacker.installComponent(new RedPSDUnpacker());
			TexturePacker.installComponent(new GdxTexturePacker());
			TextureSlicer.installComponent(new RedTextureSlicer());
			Json.installComponent(new GoogleGson());
			TextureBleed.installComponent(new RebeccaTextureBleeder());
			ImageAWT.installComponent(new RedImageAWT());
		}

		SystemSettings.setExecutionMode(ExecutionMode.PUBLIC_RELEASE);
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
		SystemSettings.setLongParameter(Assets.DEFAULT_LOGO_FADE_TIME, 5L);
		SystemSettings.setStringParameter(Assets.CLEAR_SCREEN_COLOR_ARGB, "#FF000000");
		SystemSettings.setLongParameter(GCFisher.DefaultBaitSize, 1 * 1024 * 1024);

		SystemSettings.setStringParameter(Version.Tags.PackageName, ParallaxVersion.packageName);
		SystemSettings.setStringParameter(Version.Tags.VersionCode, ParallaxVersion.versionCode + "");
		SystemSettings.setStringParameter(Version.Tags.VersionName, ParallaxVersion.versionName);

		ParallaxDesktopAssembler.deployAnalytics();

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

		{
// final List<ID> dependencies = Collections.newList();
// dependencies.add(Names.newID("org.lwjgl.LWJGLException"));
// AssetsManager.autoResolveAssets(dependencies, PackageReaderListener.DEFAULT);
		}
	}

	private void installResources () throws IOException {
		final RedResourcesManagerSpecs specs = new RedResourcesManagerSpecs();
		final RedResourcesManager res_manager = new RedResourcesManager(specs);
		ResourcesManager.installComponent(res_manager);

		final File home = LocalFileSystem.ApplicationHome();
		final File assets_folder = home.child("assets");

		if (assets_folder.exists() && assets_folder.isFolder()) {
			final Collection<ResourcesGroup> locals = res_manager.findAndInstallResources(assets_folder);
// locals.print("locals");
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
// {
// final List<String> tanks = Collections.newList("tank-0");
// final HttpURL bankURL = Http.newURL("https://s3.eu-central-1.amazonaws.com/com.red-triplane.assets/bank-lib");
// final ResourcesGroup bank = res_manager.installRemoteBank(bankURL, assets_cache_folder, tanks);
// bank.rebuildAllIndexes(null);
// }
	}

}
