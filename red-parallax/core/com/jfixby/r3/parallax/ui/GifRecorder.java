
package com.jfixby.r3.parallax.ui;

import java.io.IOException;

import com.jfixby.r3.api.ui.unit.ScreenShot;
import com.jfixby.r3.api.ui.unit.ScreenShotSpecs;
import com.jfixby.r3.api.ui.unit.UnitToolkit;
import com.jfixby.scarabei.api.desktop.GifProducer;
import com.jfixby.scarabei.api.desktop.GifProducerSpecs;
import com.jfixby.scarabei.api.desktop.ImageAWT;
import com.jfixby.scarabei.api.err.Err;
import com.jfixby.scarabei.api.file.File;
import com.jfixby.scarabei.api.file.LocalFileSystem;
import com.jfixby.scarabei.api.image.ColorMap;
import com.jfixby.scarabei.api.io.BufferOutputStream;
import com.jfixby.scarabei.api.io.IO;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.api.sys.Sys;
import com.jfixby.scarabei.api.util.JUtils;
import com.jfixby.scarabei.api.util.StateSwitcher;

public class GifRecorder {
	enum RECORDER_STATE {
		READY, RECORDING;
	}

	private static final long DELTA = 0 * 1000 / 10;

	private final StateSwitcher<RECORDER_STATE> state;
	private final File outputHome;
	private BufferOutputStream os;
	private final UnitToolkit toolkit;
	GifProducer gif;
	private File file;

	public GifRecorder (final UnitToolkit toolkit) {
		this.state = JUtils.newStateSwitcher(RECORDER_STATE.READY);
		this.outputHome = LocalFileSystem.ApplicationHome().child("gif-output");
		this.toolkit = toolkit;
	}

	public void start () {
		this.state.expectState(RECORDER_STATE.READY);
		this.state.switchState(RECORDER_STATE.RECORDING);
		try {
			this.outputHome.makeFolder();
		} catch (final IOException e) {
			e.printStackTrace();
			Err.reportError(e);
		}

		final GifProducerSpecs producerSpecs = ImageAWT.newGifProducerSpecs();

		final String fileName = Sys.SystemTime().currentTimeMillis() + ".gif";
		this.file = this.outputHome.child(fileName);

		this.os = IO.newBufferOutputStream();
		this.os.open();
		producerSpecs.setOutputStream(this.os);
		producerSpecs.setFrameBufferSize(0);

		this.gif = ImageAWT.newGifProducer(producerSpecs);
		this.gif.open();
		L.d("GIF FILE", this.file);
	}

	public void stop () {

		if (this.state.currentState() == RECORDER_STATE.READY) {
			return;
		}

		this.state.expectState(RECORDER_STATE.RECORDING);
		this.state.switchState(RECORDER_STATE.READY);
		this.gif.close();
		this.os.close();
		try {
			this.file.writeBytes(this.os.getBytes());
			L.d("GIF WRITTEN", this.file);
		} catch (final IOException e) {
			e.printStackTrace();
		}

	}

	long lastFrame = 0;

	private void record () {
		this.state.expectState(RECORDER_STATE.RECORDING);

		final long currentTime = Sys.SystemTime().currentTimeMillis();
		if (Math.abs(currentTime - this.lastFrame) < DELTA) {
			return;
		}

		this.lastFrame = currentTime;

		final ScreenShotSpecs specs = this.toolkit.newScreenShotSpecs();
		final ScreenShot shot = this.toolkit.newScreenShot(specs);
// shot.saveToFile(this.outputHome.child("sh" + Sys.SystemTime().currentTimeMillis() + ".png"));
		final ColorMap image = shot.toColorMap();

		try {
			L.d("GIF FRAME", shot);

			this.gif.append(image);
		} catch (final IOException e) {
			e.printStackTrace();
		}

	}

	public void push () {
		if (GifRecorder.this.state.currentState() == RECORDER_STATE.RECORDING) {
			GifRecorder.this.record();
		}
	}

}
