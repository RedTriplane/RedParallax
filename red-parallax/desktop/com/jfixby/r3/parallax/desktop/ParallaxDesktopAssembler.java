
package com.jfixby.r3.parallax.desktop;

import java.io.IOException;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.github.wrebecca.bleed.RebeccaTextureBleeder;
import com.jfixby.cmns.adopted.gdx.json.RedJson;
import com.jfixby.cmns.api.collisions.Collisions;
import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.file.FileSystemSandBox;
import com.jfixby.cmns.api.file.LocalFileSystem;
import com.jfixby.cmns.api.java.gc.GCFisher;
import com.jfixby.cmns.api.json.Json;
import com.jfixby.cmns.api.sys.Sys;
import com.jfixby.cmns.api.sys.settings.ExecutionMode;
import com.jfixby.cmns.api.sys.settings.SystemSettings;
import com.jfixby.psd.unpacker.api.PSDUnpacker;
import com.jfixby.psd.unpacker.core.RedPSDUnpacker;
import com.jfixby.r3.api.RedTriplane;
import com.jfixby.r3.api.RedTriplaneParams;
import com.jfixby.r3.api.logic.BusinessLogic;
import com.jfixby.r3.api.shader.R3Shader;
import com.jfixby.r3.api.ui.UI;
import com.jfixby.r3.api.ui.UIStarter;
import com.jfixby.r3.api.ui.unit.layer.LayerUtils;
import com.jfixby.r3.collide.RedCollisionsAlgebra;
import com.jfixby.r3.ext.api.font.R3Font;
import com.jfixby.r3.ext.api.scene2d.Scene2D;
import com.jfixby.r3.ext.api.text.R3Text;
import com.jfixby.r3.ext.font.gdx.ft.GdxR3Font;
import com.jfixby.r3.ext.text.red.RedTriplaneText;
import com.jfixby.r3.fokker.api.FokkerEngineAssembler;
import com.jfixby.r3.fokker.api.FokkerEngineParams;
import com.jfixby.r3.fokker.api.UnitsSpawner;
import com.jfixby.r3.fokker.api.assets.FokkerTextureLoader;
import com.jfixby.r3.fokker.backend.RedUnitSpawner;
import com.jfixby.r3.parallax.core.RedParallaxCore;
import com.jfixby.r3.ui.RedUIManager;
import com.jfixby.rana.api.asset.AssetsManager;
import com.jfixby.rana.api.asset.AssetsManagerFlags;
import com.jfixby.rana.api.pkg.ResourcesManager;
import com.jfixby.red.engine.core.Fokker;
import com.jfixby.red.engine.core.resources.RedAssetsManager;
import com.jfixby.red.engine.core.unit.layers.RedLayerUtils;
import com.jfixby.red.engine.core.unit.shader.R3FokkerShader;
import com.jfixby.red.engine.scene2d.RedScene2D;
import com.jfixby.red.filesystem.sandbox.RedFileSystemSandBox;
import com.jfixby.red.filesystem.virtual.InMemoryFileSystem;
import com.jfixby.red.triplane.resources.fsbased.RedResourcesManager;
import com.jfixby.redtriplane.fokker.assets.RedFokkerTextureLoader;
import com.jfixby.texture.slicer.api.TextureSlicer;
import com.jfixby.texture.slicer.red.RedTextureSlicer;
import com.jfixby.tools.bleed.api.TextureBleed;
import com.jfixby.tools.gdx.texturepacker.GdxTexturePacker;
import com.jfixby.tools.gdx.texturepacker.api.TexturePacker;

public class ParallaxDesktopAssembler implements FokkerEngineAssembler {

	@Override
	public void assembleEngine () {

		{
			PSDUnpacker.installComponent(new RedPSDUnpacker());
			TexturePacker.installComponent(new GdxTexturePacker());
			TextureSlicer.installComponent(new RedTextureSlicer());
			Json.installComponent(new RedJson());
			TextureBleed.installComponent(new RebeccaTextureBleeder());
		}

		this.installResources();

		Scene2D.installComponent(new RedScene2D());
		R3Font.installComponent(new GdxR3Font());
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

		ResourcesManager.registerPackageReader(Scene2D.getPackageReader());
		ResourcesManager.registerPackageReader(R3Font.getPackageReader());
		ResourcesManager.registerPackageReader(R3Text.getStringsPackageReader());
		ResourcesManager.registerPackageReader(R3Text.getTextPackageReader());
		ResourcesManager.registerPackageReader(R3Shader.getPackageReader());

		final RedUIManager tinto_ui_starter = new RedUIManager();
		UIStarter.installComponent(tinto_ui_starter);
		UI.installComponent(tinto_ui_starter);
		BusinessLogic.installComponent(new RedParallaxCore());

		Collisions.installComponent(new RedCollisionsAlgebra());
		RedTriplane.installComponent(new Fokker());

		SystemSettings.setExecutionMode(ExecutionMode.EARLY_DEVELOPMENT);
		SystemSettings.setFlag(RedTriplaneParams.PrintLogMessageOnMissingSprite, true);
		SystemSettings.setFlag(RedTriplaneParams.ExitOnMissingSprite, false);
		SystemSettings.setFlag(RedTriplaneParams.AllowMissingRaster, true);
		SystemSettings.setFlag(AssetsManager.UseAssetSandBox, false);
		SystemSettings.setFlag(AssetsManager.ReportUnusedAssets, false);
		SystemSettings.setFlag(AssetsManagerFlags.AutoresolveDependencies, true);
		SystemSettings.setFlag(R3Font.RenderRasterStrings, true);
		SystemSettings.setStringParameter(FokkerEngineParams.TextureFilter.Mag, TextureFilter.Nearest + "");
		SystemSettings.setStringParameter(FokkerEngineParams.TextureFilter.Min, TextureFilter.Nearest + "");
		SystemSettings.setStringParameter(RedTriplaneParams.DefaultFont, "Arial");
		SystemSettings.setLongParameter(RedTriplaneParams.DEFAULT_LOGO_FADE_TIME, 2000L);
		SystemSettings.setStringParameter(RedTriplaneParams.CLEAR_SCREEN_COLOR_ARGB, "#FFeeeeee");
		SystemSettings.setLongParameter(GCFisher.DefaultBaitSize, 1 * 1024 * 1024);

		UnitsSpawner.installComponent(new RedUnitSpawner());

	}

	private void installResources () {

		SystemSettings.setStringParameter(RedTriplaneParams.ASSET_INFO_TAG, "<no assets info>");

		final RedResourcesManager res_manager = new RedResourcesManager();
		ResourcesManager.installComponent(res_manager);

		final File assets_folder = LocalFileSystem.ApplicationHome().child("assets");

		if (assets_folder.exists() && assets_folder.isFolder()) {
			res_manager.findAndInstallBanks(assets_folder);
		}

		res_manager.tryToLoadConfigFile();

// this.loadConfig(res_manager);

	}

// private void printAssetsInfo (final File dev_assets_home) {
//
// final File assets_file = dev_assets_home.child(AssetsInfo.FILE_NAME);
// // String super_file = fh.file().getAbsolutePath();
// if (!assets_file.exists()) {
// return;
// }
// assets_file.checkExists();
//
// AssetsInfo info;
// try {
// info = assets_file.readData(AssetsInfo.class);
// } catch (final IOException e) {
// e.printStackTrace();
// return;
// }
//
// SystemSettings.setStringParameter(RedTriplaneParams.ASSET_INFO_TAG, info.toString());
//
// info.print();
// }
	private File preload (File dev_assets_home) {
		final InMemoryFileSystem virtualFS = new InMemoryFileSystem();
		try {
			virtualFS.copyFolderContentsToFolder(dev_assets_home, virtualFS.ROOT());
			dev_assets_home = virtualFS.ROOT();
		} catch (final IOException e) {
			e.printStackTrace();
			Sys.exit();
		}
		return dev_assets_home;
	}

}
