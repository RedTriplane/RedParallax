
package com.jfixby.r3.parallax.ui;

import com.jfixby.r3.api.ui.unit.input.MouseMovedEvent;
import com.jfixby.r3.api.ui.unit.input.TouchDownEvent;
import com.jfixby.r3.api.ui.unit.input.TouchDraggedEvent;
import com.jfixby.r3.api.ui.unit.input.TouchUpEvent;
import com.jfixby.r3.api.ui.unit.user.MouseInputEventListener;
import com.jfixby.scarabei.api.floatn.Float2;
import com.jfixby.scarabei.api.geometry.Geometry;

public class MouseCapture implements MouseInputEventListener {

	boolean mouse_pressed = false;
	final Float2 mouseStart = Geometry.newFloat2();
	final Float2 mouseCurrent = Geometry.newFloat2();
	final Float2 mouseDelta = Geometry.newFloat2();
	final Float2 globalDelta = Geometry.newFloat2();
	final Float2 tmp = Geometry.newFloat2();
	private final ParallaxUI parallaxUI;

	@Override
	public boolean onMouseMoved (final MouseMovedEvent input_event) {
// L.d(input_event);
		this.mouse_pressed = false;
		return true;
	}

	@Override
	public boolean onTouchDown (final TouchDownEvent input_event) {
// L.d(input_event);
		this.mouse_pressed = true;
		this.mouseStart.set(input_event.getCanvasPosition());
		this.mouseCurrent.set(input_event.getCanvasPosition());
		this.parallaxUI.animating = false;
		return true;
	}

	@Override
	public boolean onTouchUp (final TouchUpEvent input_event) {
// L.d(input_event);
		this.mouseCurrent.set(input_event.getCanvasPosition());
		this.mouse_pressed = !true;
		this.updateMouseDelta();
		this.globalDelta.setLinearSum(this.globalDelta, 1, this.mouseDelta, 1);
		this.parallaxUI.animating = false;
		return true;
	}

	@Override
	public boolean onTouchDragged (final TouchDraggedEvent input_event) {
// L.d(input_event);
		this.mouseCurrent.set(input_event.getCanvasPosition());
		this.updateMouseDelta();
		this.mouse_pressed = true;
		this.parallaxUI.animating = false;
		return true;
	}

	public MouseCapture (final ParallaxUI parallaxUI) {
		this.parallaxUI = parallaxUI;
	}

	private void updateMouseDelta () {
		this.mouseDelta.setLinearSum(this.mouseCurrent, 1, this.mouseStart, -1);
		this.tmp.setLinearSum(this.globalDelta, 1, this.mouseDelta, 1);
		this.tmp.scaleXY(-1 / this.parallaxUI.getParallaxWidth());
		this.parallaxUI.setParallax(this.tmp);

	}

}
