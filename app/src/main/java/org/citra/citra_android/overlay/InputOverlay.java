/**
 * Copyright 2013 Dolphin Emulator Project
 * Licensed under GPLv2+
 * Refer to the license.txt file included.
 */

package org.citra.citra_android.overlay;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Display;
import android.view.View.OnTouchListener;

import org.citra.citra_android.NativeLibrary;
import org.citra.citra_android.NativeLibrary.ButtonState;
import org.citra.citra_android.NativeLibrary.ButtonType;
import org.citra.citra_android.R;

import java.util.HashSet;
import java.util.Set;

/**
 * Draws the interactive input overlay on top of the
 * {@link SurfaceView} that is rendering emulation.
 */
public final class InputOverlay extends SurfaceView implements OnTouchListener
{
  private final Set<InputOverlayDrawableButton> overlayButtons = new HashSet<>();
  private final Set<InputOverlayDrawableDpad> overlayDpads = new HashSet<>();
  private final Set<InputOverlayDrawableJoystick> overlayJoysticks = new HashSet<>();

  private boolean mIsInEditMode = false;
  private InputOverlayDrawableButton mButtonBeingConfigured;
  private InputOverlayDrawableDpad mDpadBeingConfigured;
  private InputOverlayDrawableJoystick mJoystickBeingConfigured;

  private SharedPreferences mPreferences;

  /**
   * Resizes a {@link Bitmap} by a given scale factor
   *
   * @param context The current {@link Context}
   * @param bitmap  The {@link Bitmap} to scale.
   * @param scale   The scale factor for the bitmap.
   * @return The scaled {@link Bitmap}
   */
  public static Bitmap resizeBitmap(Context context, Bitmap bitmap, float scale)
  {
    // Determine the button size based on the smaller screen dimension.
    // This makes sure the buttons are the same size in both portrait and landscape.
    DisplayMetrics dm = context.getResources().getDisplayMetrics();
    int minDimension = Math.min(dm.widthPixels, dm.heightPixels);

    return Bitmap.createScaledBitmap(bitmap,
            (int) (minDimension * scale),
            (int) (minDimension * scale),
            true);
  }

  /**
   * Constructor
   *
   * @param context The current {@link Context}.
   * @param attrs   {@link AttributeSet} for parsing XML attributes.
   */
  public InputOverlay(Context context, AttributeSet attrs)
  {
    super(context, attrs);

    mPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
    	if(!mPreferences.getBoolean("OverlayInit", false))
	    defaultOverlay();
    // Load the controls.
    refreshControls();

    // Set the on touch listener.
    setOnTouchListener(this);

    // Force draw
    setWillNotDraw(false);

    // Request focus for the overlay so it has priority on presses.
    requestFocus();
  }

  @Override
  public void draw(Canvas canvas)
  {
    super.draw(canvas);

    for (InputOverlayDrawableButton button : overlayButtons)
    {
      button.draw(canvas);
    }

    for (InputOverlayDrawableDpad dpad : overlayDpads)
    {
      dpad.draw(canvas);
    }

    for (InputOverlayDrawableJoystick joystick : overlayJoysticks)
    {
      joystick.draw(canvas);
    }
  }

  @Override
  public boolean onTouch(View v, MotionEvent event)
  {
    if (isInEditMode())
    {
      return onTouchWhileEditing(event);
    }

    int pointerIndex = event.getActionIndex();

    if (mPreferences.getBoolean("isTouchEnabled", true))
    {
      switch (event.getAction())
      {
        case MotionEvent.ACTION_DOWN:
        case MotionEvent.ACTION_POINTER_DOWN:
          NativeLibrary.onTouchEvent(event.getX(pointerIndex), event.getY(pointerIndex), true);
          break;
        case MotionEvent.ACTION_MOVE:
          NativeLibrary.onTouchMoved(event.getX(), event.getY());
          break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_POINTER_UP:
          // We dont really care where the touch has been released. We only care whether it has been
          // released or not.
          NativeLibrary.onTouchEvent(0, 0, false);
          break;
      }
    }

    for (InputOverlayDrawableButton button : overlayButtons)
    {
      // Determine the button state to apply based on the MotionEvent action flag.
      switch (event.getAction() & MotionEvent.ACTION_MASK)
      {
        case MotionEvent.ACTION_DOWN:
        case MotionEvent.ACTION_POINTER_DOWN:
          // If a pointer enters the bounds of a button, press that button.
          if (button.getBounds()
                  .contains((int) event.getX(pointerIndex), (int) event.getY(pointerIndex)))
          {
            button.setPressedState(true);
            button.setTrackId(event.getPointerId(pointerIndex));
            NativeLibrary.onGamePadEvent(NativeLibrary.TouchScreenDevice, button.getId(),
                    ButtonState.PRESSED);
          }
          break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_POINTER_UP:
          // If a pointer ends, release the button it was pressing.
          if (button.getTrackId() == event.getPointerId(pointerIndex))
          {
            button.setPressedState(false);
            NativeLibrary.onGamePadEvent(NativeLibrary.TouchScreenDevice, button.getId(),
                    ButtonState.RELEASED);
          }
          break;
      }
    }

    for (InputOverlayDrawableDpad dpad : overlayDpads)
    {
      // Determine the button state to apply based on the MotionEvent action flag.
      switch (event.getAction() & MotionEvent.ACTION_MASK)
      {
        case MotionEvent.ACTION_DOWN:
        case MotionEvent.ACTION_POINTER_DOWN:
          // If a pointer enters the bounds of a button, press that button.
          if (dpad.getBounds()
                  .contains((int) event.getX(pointerIndex), (int) event.getY(pointerIndex)))
          {
            boolean up = false;
            boolean down = false;
            boolean left = false;
            boolean right = false;
            if (dpad.getBounds().top + (dpad.getHeight() / 3) > (int) event.getY(pointerIndex))
            {
              NativeLibrary.onGamePadEvent(NativeLibrary.TouchScreenDevice, dpad.getId(0),
                      ButtonState.PRESSED);
              up = true;
            }
            if (dpad.getBounds().bottom - (dpad.getHeight() / 3) < (int) event.getY(pointerIndex))
            {
              NativeLibrary.onGamePadEvent(NativeLibrary.TouchScreenDevice, dpad.getId(1),
                      ButtonState.PRESSED);
              down = true;
            }
            if (dpad.getBounds().left + (dpad.getWidth() / 3) > (int) event.getX(pointerIndex))
            {
              NativeLibrary.onGamePadEvent(NativeLibrary.TouchScreenDevice, dpad.getId(2),
                      ButtonState.PRESSED);
              left = true;
            }
            if (dpad.getBounds().right - (dpad.getWidth() / 3) < (int) event.getX(pointerIndex))
            {
              NativeLibrary.onGamePadEvent(NativeLibrary.TouchScreenDevice, dpad.getId(3),
                      ButtonState.PRESSED);
              right = true;
            }

            setDpadState(dpad, up, down, left, right);
            dpad.setTrackId(event.getPointerId(pointerIndex));
          }
          break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_POINTER_UP:
          // If a pointer ends, release the buttons.
          if (dpad.getTrackId() == event.getPointerId(pointerIndex))
          {
            for (int i = 0; i < 4; i++)
            {
              dpad.setState(InputOverlayDrawableDpad.STATE_DEFAULT);
              NativeLibrary.onGamePadEvent(NativeLibrary.TouchScreenDevice, dpad.getId(i),
                      ButtonState.RELEASED);
            }
          }
          break;
      }
    }

    for (InputOverlayDrawableJoystick joystick : overlayJoysticks)
    {
      joystick.TrackEvent(event);
      int axisID = joystick.getId();
      float[] axises = joystick.getAxisValues();

      NativeLibrary
              .onGamePadMoveEvent(NativeLibrary.TouchScreenDevice, axisID, axises[0], axises[1]);
    }

    invalidate();

    return true;
  }

  public boolean onTouchWhileEditing(MotionEvent event)
  {
    int pointerIndex = event.getActionIndex();
    int fingerPositionX = (int) event.getX(pointerIndex);
    int fingerPositionY = (int) event.getY(pointerIndex);

    // Maybe combine Button and Joystick as subclasses of the same parent?
    // Or maybe create an interface like IMoveableHUDControl?

    for (InputOverlayDrawableButton button : overlayButtons)
    {
      // Determine the button state to apply based on the MotionEvent action flag.
      switch (event.getAction() & MotionEvent.ACTION_MASK)
      {
        case MotionEvent.ACTION_DOWN:
        case MotionEvent.ACTION_POINTER_DOWN:
          // If no button is being moved now, remember the currently touched button to move.
          if (mButtonBeingConfigured == null &&
                  button.getBounds().contains(fingerPositionX, fingerPositionY))
          {
            mButtonBeingConfigured = button;
            mButtonBeingConfigured.onConfigureTouch(event);
          }
          break;
        case MotionEvent.ACTION_MOVE:
          if (mButtonBeingConfigured != null)
          {
            mButtonBeingConfigured.onConfigureTouch(event);
            invalidate();
            return true;
          }
          break;

        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_POINTER_UP:
          if (mButtonBeingConfigured == button)
          {
            // Persist button position by saving new place.
            saveControlPosition(mButtonBeingConfigured.getId(),
                    mButtonBeingConfigured.getBounds().left,
                    mButtonBeingConfigured.getBounds().top);
            mButtonBeingConfigured = null;
          }
          break;
      }
    }

    for (InputOverlayDrawableDpad dpad : overlayDpads)
    {
      // Determine the button state to apply based on the MotionEvent action flag.
      switch (event.getAction() & MotionEvent.ACTION_MASK)
      {
        case MotionEvent.ACTION_DOWN:
        case MotionEvent.ACTION_POINTER_DOWN:
          // If no button is being moved now, remember the currently touched button to move.
          if (mButtonBeingConfigured == null &&
                  dpad.getBounds().contains(fingerPositionX, fingerPositionY))
          {
            mDpadBeingConfigured = dpad;
            mDpadBeingConfigured.onConfigureTouch(event);
          }
          break;
        case MotionEvent.ACTION_MOVE:
          if (mDpadBeingConfigured != null)
          {
            mDpadBeingConfigured.onConfigureTouch(event);
            invalidate();
            return true;
          }
          break;

        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_POINTER_UP:
          if (mDpadBeingConfigured == dpad)
          {
            // Persist button position by saving new place.
            saveControlPosition(mDpadBeingConfigured.getId(0),
                    mDpadBeingConfigured.getBounds().left, mDpadBeingConfigured.getBounds().top);
            mDpadBeingConfigured = null;
          }
          break;
      }
    }

    for (InputOverlayDrawableJoystick joystick : overlayJoysticks)
    {
      switch (event.getAction())
      {
        case MotionEvent.ACTION_DOWN:
        case MotionEvent.ACTION_POINTER_DOWN:
          if (mJoystickBeingConfigured == null &&
                  joystick.getBounds().contains(fingerPositionX, fingerPositionY))
          {
            mJoystickBeingConfigured = joystick;
            mJoystickBeingConfigured.onConfigureTouch(event);
          }
          break;
        case MotionEvent.ACTION_MOVE:
          if (mJoystickBeingConfigured != null)
          {
            mJoystickBeingConfigured.onConfigureTouch(event);
            invalidate();
          }
          break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_POINTER_UP:
          if (mJoystickBeingConfigured != null)
          {
            saveControlPosition(mJoystickBeingConfigured.getId(),
                    mJoystickBeingConfigured.getBounds().left,
                    mJoystickBeingConfigured.getBounds().top);
            mJoystickBeingConfigured = null;
          }
          break;
      }
    }

    return true;
  }

  private void setDpadState(InputOverlayDrawableDpad dpad, boolean up, boolean down, boolean left,
          boolean right)
  {
    if (up)
    {
      if (left)
        dpad.setState(InputOverlayDrawableDpad.STATE_PRESSED_UP_LEFT);
      else if (right)
        dpad.setState(InputOverlayDrawableDpad.STATE_PRESSED_UP_RIGHT);
      else
        dpad.setState(InputOverlayDrawableDpad.STATE_PRESSED_UP);
    }
    else if (down)
    {
      if (left)
        dpad.setState(InputOverlayDrawableDpad.STATE_PRESSED_DOWN_LEFT);
      else if (right)
        dpad.setState(InputOverlayDrawableDpad.STATE_PRESSED_DOWN_RIGHT);
      else
        dpad.setState(InputOverlayDrawableDpad.STATE_PRESSED_DOWN);
    }
    else if (left)
    {
      dpad.setState(InputOverlayDrawableDpad.STATE_PRESSED_LEFT);
    }
    else if (right)
    {
      dpad.setState(InputOverlayDrawableDpad.STATE_PRESSED_RIGHT);
    }
  }

  private void add3dsOverlayControls()
  {
    if (mPreferences.getBoolean("buttonToggle3ds0", true))
    {
      overlayButtons.add(initializeOverlayButton(getContext(), R.drawable.classic_a,
              R.drawable.classic_a_pressed, ButtonType.N3DS_BUTTON_A));
    }
    if (mPreferences.getBoolean("buttonToggle3ds1", true))
    {
      overlayButtons.add(initializeOverlayButton(getContext(), R.drawable.classic_b,
              R.drawable.classic_b_pressed, ButtonType.N3DS_BUTTON_B));
    }
    if (mPreferences.getBoolean("buttonToggle3ds2", true))
    {
      overlayButtons.add(initializeOverlayButton(getContext(), R.drawable.classic_x,
              R.drawable.classic_x_pressed, ButtonType.N3DS_BUTTON_X));
    }
    if (mPreferences.getBoolean("buttonToggle3ds3", true))
    {
      overlayButtons.add(initializeOverlayButton(getContext(), R.drawable.classic_y,
              R.drawable.classic_y_pressed, ButtonType.N3DS_BUTTON_Y));
    }
    if (mPreferences.getBoolean("buttonToggle3ds4", true))
    {
      overlayButtons.add(initializeOverlayButton(getContext(), R.drawable.classic_l,
              R.drawable.classic_l_pressed, ButtonType.N3DS_TRIGGER_L));
    }
    if (mPreferences.getBoolean("buttonToggle3ds5", true))
    {
      overlayButtons.add(initializeOverlayButton(getContext(), R.drawable.classic_r,
              R.drawable.classic_r_pressed, ButtonType.N3DS_TRIGGER_R));
    }
    if (mPreferences.getBoolean("buttonToggle3ds6", true))
    {
      overlayButtons.add(initializeOverlayButton(getContext(), R.drawable.classic_zl,
              R.drawable.classic_zl_pressed, ButtonType.N3DS_BUTTON_ZL));
    }
    if (mPreferences.getBoolean("buttonToggle3ds7", true))
    {
      overlayButtons.add(initializeOverlayButton(getContext(), R.drawable.classic_zr,
              R.drawable.classic_zr_pressed, ButtonType.N3DS_BUTTON_ZR));
    }
    if (mPreferences.getBoolean("buttonToggle3ds8", true))
    {
      overlayButtons.add(initializeOverlayButton(getContext(), R.drawable.gcpad_start,
              R.drawable.gcpad_start_pressed, ButtonType.N3DS_BUTTON_START));
    }
    if (mPreferences.getBoolean("buttonToggle3ds9", true))
    {
      overlayButtons.add(initializeOverlayButton(getContext(), R.drawable.n3ds_select,
              R.drawable.n3ds_select_pressed, ButtonType.N3DS_BUTTON_SELECT));
    }
    if (mPreferences.getBoolean("buttonToggle3ds10", true))
    {
      overlayButtons.add(initializeOverlayButton(getContext(), R.drawable.wiimote_home,
              R.drawable.wiimote_home_pressed, ButtonType.N3DS_BUTTON_HOME));
    }
    if (mPreferences.getBoolean("buttonToggle3ds11", true))
    {
      overlayDpads.add(initializeOverlayDpad(getContext(), R.drawable.gcwii_dpad,
              R.drawable.gcwii_dpad_pressed_one_direction,
              R.drawable.gcwii_dpad_pressed_two_directions,
              ButtonType.N3DS_DPAD_UP, ButtonType.N3DS_DPAD_DOWN,
              ButtonType.N3DS_DPAD_LEFT, ButtonType.N3DS_DPAD_RIGHT));
    }
    if (mPreferences.getBoolean("buttonToggle3ds12", true))
    {
      overlayJoysticks.add(initializeOverlayJoystick(getContext(), R.drawable.gcwii_joystick_range,
              R.drawable.gcwii_joystick, R.drawable.gcwii_joystick_pressed,
              ButtonType.N3DS_STICK_LEFT));
    }
    if (mPreferences.getBoolean("buttonToggle3ds13", true))
    {
      overlayJoysticks.add(initializeOverlayJoystick(getContext(), R.drawable.gcwii_joystick_range,
              R.drawable.gcpad_c, R.drawable.gcpad_c_pressed, ButtonType.N3DS_STICK_C));
    }
  }

  public void refreshControls()
  {
    // Remove all the overlay buttons from the HashSet.
    overlayButtons.removeAll(overlayButtons);
    overlayDpads.removeAll(overlayDpads);
    overlayJoysticks.removeAll(overlayJoysticks);

    add3dsOverlayControls();

    invalidate();
  }

  private void saveControlPosition(int sharedPrefsId, int x, int y)
  {
    final SharedPreferences sPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
    SharedPreferences.Editor sPrefsEditor = sPrefs.edit();
    sPrefsEditor.putFloat(sharedPrefsId + "-X", x);
    sPrefsEditor.putFloat(sharedPrefsId + "-Y", y);
    sPrefsEditor.apply();
  }

  /**
   * Initializes an InputOverlayDrawableButton, given by resId, with all of the
   * parameters set for it to be properly shown on the InputOverlay.
   * <p>
   * This works due to the way the X and Y coordinates are stored within
   * the {@link SharedPreferences}.
   * <p>
   * In the input overlay configuration menu,
   * once a touch event begins and then ends (ie. Organizing the buttons to one's own liking for the overlay).
   * the X and Y coordinates of the button at the END of its touch event
   * (when you remove your finger/stylus from the touchscreen) are then stored
   * within a SharedPreferences instance so that those values can be retrieved here.
   * <p>
   * This has a few benefits over the conventional way of storing the values
   * (ie. within the Dolphin ini file).
   * <ul>
   * <li>No native calls</li>
   * <li>Keeps Android-only values inside the Android environment</li>
   * </ul>
   * <p>
   * Technically no modifications should need to be performed on the returned
   * InputOverlayDrawableButton. Simply add it to the HashSet of overlay items and wait
   * for Android to call the onDraw method.
   *
   * @param context      The current {@link Context}.
   * @param defaultResId The resource ID of the {@link Drawable} to get the {@link Bitmap} of (Default State).
   * @param pressedResId The resource ID of the {@link Drawable} to get the {@link Bitmap} of (Pressed State).
   * @param buttonId     Identifier for determining what type of button the initialized InputOverlayDrawableButton represents.
   * @return An {@link InputOverlayDrawableButton} with the correct drawing bounds set.
   */
  private static InputOverlayDrawableButton initializeOverlayButton(Context context,
          int defaultResId, int pressedResId, int buttonId)
  {
    // Resources handle for fetching the initial Drawable resource.
    final Resources res = context.getResources();

    // SharedPreference to retrieve the X and Y coordinates for the InputOverlayDrawableButton.
    final SharedPreferences sPrefs = PreferenceManager.getDefaultSharedPreferences(context);

    // Decide scale based on button ID and user preference
    float scale;

    switch (buttonId)
    {
      case ButtonType.N3DS_BUTTON_HOME:
      case ButtonType.N3DS_BUTTON_START:
      case ButtonType.N3DS_BUTTON_SELECT:
        scale = 0.0625f;
        break;
      case ButtonType.N3DS_TRIGGER_L:
      case ButtonType.N3DS_TRIGGER_R:
      case ButtonType.N3DS_BUTTON_ZL:
      case ButtonType.N3DS_BUTTON_ZR:
        scale = 0.25f;
        break;
      default:
        scale = 0.125f;
        break;
    }

    scale *= (sPrefs.getInt("controlScale", 50) + 50);
    scale /= 100;

    // Initialize the InputOverlayDrawableButton.
    final Bitmap defaultStateBitmap =
            resizeBitmap(context, BitmapFactory.decodeResource(res, defaultResId), scale);
    final Bitmap pressedStateBitmap =
            resizeBitmap(context, BitmapFactory.decodeResource(res, pressedResId), scale);
    final InputOverlayDrawableButton overlayDrawable =
            new InputOverlayDrawableButton(res, defaultStateBitmap, pressedStateBitmap, buttonId);

    // The X and Y coordinates of the InputOverlayDrawableButton on the InputOverlay.
    // These were set in the input overlay configuration menu.
    int drawableX = (int) sPrefs.getFloat(buttonId + "-X", 0f);
    int drawableY = (int) sPrefs.getFloat(buttonId + "-Y", 0f);

    int width = overlayDrawable.getWidth();
    int height = overlayDrawable.getHeight();

    // Now set the bounds for the InputOverlayDrawableButton.
    // This will dictate where on the screen (and the what the size) the InputOverlayDrawableButton will be.
    overlayDrawable.setBounds(drawableX, drawableY, drawableX + width, drawableY + height);

    // Need to set the image's position
    overlayDrawable.setPosition(drawableX, drawableY);

    return overlayDrawable;
  }

  /**
   * Initializes an {@link InputOverlayDrawableDpad}
   *
   * @param context                   The current {@link Context}.
   * @param defaultResId              The {@link Bitmap} resource ID of the default sate.
   * @param pressedOneDirectionResId  The {@link Bitmap} resource ID of the pressed sate in one direction.
   * @param pressedTwoDirectionsResId The {@link Bitmap} resource ID of the pressed sate in two directions.
   * @param buttonUp                  Identifier for the up button.
   * @param buttonDown                Identifier for the down button.
   * @param buttonLeft                Identifier for the left button.
   * @param buttonRight               Identifier for the right button.
   * @return the initialized {@link InputOverlayDrawableDpad}
   */
  private static InputOverlayDrawableDpad initializeOverlayDpad(Context context,
          int defaultResId,
          int pressedOneDirectionResId,
          int pressedTwoDirectionsResId,
          int buttonUp,
          int buttonDown,
          int buttonLeft,
          int buttonRight)
  {
    // Resources handle for fetching the initial Drawable resource.
    final Resources res = context.getResources();

    // SharedPreference to retrieve the X and Y coordinates for the InputOverlayDrawableDpad.
    final SharedPreferences sPrefs = PreferenceManager.getDefaultSharedPreferences(context);

    // Decide scale based on button ID and user preference
    float scale;

    switch (buttonUp)
    {
      case ButtonType.N3DS_DPAD_UP:
        scale = 0.275f;
        break;
      default:
        scale = 0.2125f;
        break;
    }

    scale *= (sPrefs.getInt("controlScale", 50) + 50);
    scale /= 100;

    // Initialize the InputOverlayDrawableDpad.
    final Bitmap defaultStateBitmap =
            resizeBitmap(context, BitmapFactory.decodeResource(res, defaultResId), scale);
    final Bitmap pressedOneDirectionStateBitmap =
            resizeBitmap(context, BitmapFactory.decodeResource(res, pressedOneDirectionResId),
                    scale);
    final Bitmap pressedTwoDirectionsStateBitmap =
            resizeBitmap(context, BitmapFactory.decodeResource(res, pressedTwoDirectionsResId),
                    scale);
    final InputOverlayDrawableDpad overlayDrawable =
            new InputOverlayDrawableDpad(res, defaultStateBitmap,
                    pressedOneDirectionStateBitmap, pressedTwoDirectionsStateBitmap,
                    buttonUp, buttonDown, buttonLeft, buttonRight);

    // The X and Y coordinates of the InputOverlayDrawableDpad on the InputOverlay.
    // These were set in the input overlay configuration menu.
    int drawableX = (int) sPrefs.getFloat(buttonUp + "-X", 0f);
    int drawableY = (int) sPrefs.getFloat(buttonUp + "-Y", 0f);

    int width = overlayDrawable.getWidth();
    int height = overlayDrawable.getHeight();

    // Now set the bounds for the InputOverlayDrawableDpad.
    // This will dictate where on the screen (and the what the size) the InputOverlayDrawableDpad will be.
    overlayDrawable.setBounds(drawableX, drawableY, drawableX + width, drawableY + height);

    // Need to set the image's position
    overlayDrawable.setPosition(drawableX, drawableY);

    return overlayDrawable;
  }

  /**
   * Initializes an {@link InputOverlayDrawableJoystick}
   *
   * @param context         The current {@link Context}
   * @param resOuter        Resource ID for the outer image of the joystick (the static image that shows the circular bounds).
   * @param defaultResInner Resource ID for the default inner image of the joystick (the one you actually move around).
   * @param pressedResInner Resource ID for the pressed inner image of the joystick.
   * @param joystick        Identifier for which joystick this is.
   * @return the initialized {@link InputOverlayDrawableJoystick}.
   */
  private static InputOverlayDrawableJoystick initializeOverlayJoystick(Context context,
          int resOuter, int defaultResInner, int pressedResInner, int joystick)
  {
    // Resources handle for fetching the initial Drawable resource.
    final Resources res = context.getResources();

    // SharedPreference to retrieve the X and Y coordinates for the InputOverlayDrawableJoystick.
    final SharedPreferences sPrefs = PreferenceManager.getDefaultSharedPreferences(context);

    // Decide scale based on user preference
    float scale = 0.275f;
    scale *= (sPrefs.getInt("controlScale", 50) + 50);
    scale /= 100;

    // Initialize the InputOverlayDrawableJoystick.
    final Bitmap bitmapOuter =
            resizeBitmap(context, BitmapFactory.decodeResource(res, resOuter), scale);
    final Bitmap bitmapInnerDefault = BitmapFactory.decodeResource(res, defaultResInner);
    final Bitmap bitmapInnerPressed = BitmapFactory.decodeResource(res, pressedResInner);

    // The X and Y coordinates of the InputOverlayDrawableButton on the InputOverlay.
    // These were set in the input overlay configuration menu.
    int drawableX = (int) sPrefs.getFloat(joystick + "-X", 0f);
    int drawableY = (int) sPrefs.getFloat(joystick + "-Y", 0f);

    // Decide inner scale based on joystick ID
    float innerScale;

    switch (joystick)
    {
      case ButtonType.N3DS_STICK_C:
        innerScale = 1.833f;
        break;
      default:
        innerScale = 1.375f;
        break;
    }

    // Now set the bounds for the InputOverlayDrawableJoystick.
    // This will dictate where on the screen (and the what the size) the InputOverlayDrawableJoystick will be.
    int outerSize = bitmapOuter.getWidth();
    Rect outerRect = new Rect(drawableX, drawableY, drawableX + outerSize, drawableY + outerSize);
    Rect innerRect = new Rect(0, 0, (int) (outerSize / innerScale), (int) (outerSize / innerScale));

    // Send the drawableId to the joystick so it can be referenced when saving control position.
    final InputOverlayDrawableJoystick overlayDrawable
            = new InputOverlayDrawableJoystick(res, bitmapOuter,
            bitmapInnerDefault, bitmapInnerPressed,
            outerRect, innerRect, joystick);

    // Need to set the image's position
    overlayDrawable.setPosition(drawableX, drawableY);

    return overlayDrawable;
  }

  public void setIsInEditMode(boolean isInEditMode)
  {
    mIsInEditMode = isInEditMode;
  }

  private void defaultOverlay()
  {
    // It's possible that a user has created their overlay before this was added
    // Only change the overlay if the 'A' button is not in the upper corner.
    // GameCube
    if (mPreferences.getFloat(ButtonType.N3DS_BUTTON_A + "-X", 0f) == 0f)
      {
        N3DS_DefaultOverlay();
      }
    SharedPreferences.Editor sPrefsEditor = mPreferences.edit();
    sPrefsEditor.putBoolean("OverlayInit", true);
    sPrefsEditor.apply();
  }

  private void N3DS_DefaultOverlay()
  {
    SharedPreferences.Editor sPrefsEditor = mPreferences.edit();
    // Get screen size
    Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
    DisplayMetrics outMetrics = new DisplayMetrics();
    display.getMetrics(outMetrics);
    float maxX = outMetrics.heightPixels;
    float maxY = outMetrics.widthPixels;
    // Height and width changes depending on orientation. Use the larger value for height.
    if (maxY > maxX)
     {
        float tmp = maxX;
        maxX = maxY;
        maxY = tmp;
     }
    Resources res = getResources();
    // Each value is a percent from max X/Y stored as an int. Have to bring that value down
    // to a decimal before multiplying by MAX X/Y.
    sPrefsEditor.putFloat(ButtonType.N3DS_BUTTON_A + "-X",  (((float)res.getInteger(R.integer.N3DS_BUTTON_A_X) / 1000) * maxX));
    sPrefsEditor.putFloat(ButtonType.N3DS_BUTTON_A + "-Y",  (((float)res.getInteger(R.integer.N3DS_BUTTON_A_Y) / 1000) * maxY));
    sPrefsEditor.putFloat(ButtonType.N3DS_BUTTON_B + "-X", (((float)res.getInteger(R.integer.N3DS_BUTTON_B_X) / 1000) * maxX));
    sPrefsEditor.putFloat(ButtonType.N3DS_BUTTON_B + "-Y", (((float)res.getInteger(R.integer.N3DS_BUTTON_B_Y) / 1000) * maxY));
    sPrefsEditor.putFloat(ButtonType.N3DS_BUTTON_X + "-X", (((float)res.getInteger(R.integer.N3DS_BUTTON_X_X) / 1000) * maxX));
    sPrefsEditor.putFloat(ButtonType.N3DS_BUTTON_X + "-Y", (((float)res.getInteger(R.integer.N3DS_BUTTON_X_Y) / 1000) * maxY));
    sPrefsEditor.putFloat(ButtonType.N3DS_BUTTON_Y + "-X", (((float)res.getInteger(R.integer.N3DS_BUTTON_Y_X) / 1000) * maxX));
    sPrefsEditor.putFloat(ButtonType.N3DS_BUTTON_Y + "-Y", (((float)res.getInteger(R.integer.N3DS_BUTTON_Y_Y) / 1000) * maxY));
    sPrefsEditor.putFloat(ButtonType.N3DS_BUTTON_ZL + "-X",  (((float)res.getInteger(R.integer.N3DS_BUTTON_ZL_X) / 1000) * maxX));
    sPrefsEditor.putFloat(ButtonType.N3DS_BUTTON_ZL + "-Y",  (((float)res.getInteger(R.integer.N3DS_BUTTON_ZL_Y) / 1000) * maxY));
    sPrefsEditor.putFloat(ButtonType.N3DS_BUTTON_ZR + "-X",  (((float)res.getInteger(R.integer.N3DS_BUTTON_ZR_X) / 1000) * maxX));
    sPrefsEditor.putFloat(ButtonType.N3DS_BUTTON_ZR + "-Y",  (((float)res.getInteger(R.integer.N3DS_BUTTON_ZR_Y) / 1000) * maxY));
    sPrefsEditor.putFloat(ButtonType.N3DS_DPAD_UP + "-X", (((float)res.getInteger(R.integer.N3DS_BUTTON_UP_X) / 1000) * maxX));
    sPrefsEditor.putFloat(ButtonType.N3DS_DPAD_UP + "-Y", (((float)res.getInteger(R.integer.N3DS_BUTTON_UP_Y) / 1000) * maxY));
    sPrefsEditor.putFloat(ButtonType.N3DS_TRIGGER_L + "-X", (((float)res.getInteger(R.integer.N3DS_TRIGGER_L_X) / 1000) * maxX));
    sPrefsEditor.putFloat(ButtonType.N3DS_TRIGGER_L + "-Y", (((float)res.getInteger(R.integer.N3DS_TRIGGER_L_Y) / 1000) * maxY));
    sPrefsEditor.putFloat(ButtonType.N3DS_TRIGGER_R + "-X", (((float)res.getInteger(R.integer.N3DS_TRIGGER_R_X) / 1000) * maxX));
    sPrefsEditor.putFloat(ButtonType.N3DS_TRIGGER_R + "-Y", (((float)res.getInteger(R.integer.N3DS_TRIGGER_R_Y) / 1000) * maxY));
    sPrefsEditor.putFloat(ButtonType.N3DS_BUTTON_START + "-X", (((float)res.getInteger(R.integer.N3DS_BUTTON_START_X) / 1000) * maxX));
    sPrefsEditor.putFloat(ButtonType.N3DS_BUTTON_START + "-Y", (((float)res.getInteger(R.integer.N3DS_BUTTON_START_Y) / 1000) * maxY));
    sPrefsEditor.putFloat(ButtonType.N3DS_BUTTON_SELECT + "-X", (((float)res.getInteger(R.integer.N3DS_BUTTON_SELECT_X) / 1000) * maxX));
    sPrefsEditor.putFloat(ButtonType.N3DS_BUTTON_SELECT + "-Y", (((float)res.getInteger(R.integer.N3DS_BUTTON_SELECT_Y) / 1000) * maxY));
    sPrefsEditor.putFloat(ButtonType.N3DS_BUTTON_HOME + "-X", (((float)res.getInteger(R.integer.N3DS_BUTTON_HOME_X) / 1000) * maxX));
    sPrefsEditor.putFloat(ButtonType.N3DS_BUTTON_HOME + "-Y", (((float)res.getInteger(R.integer.N3DS_BUTTON_HOME_Y) / 1000) * maxY));
    sPrefsEditor.putFloat(ButtonType.N3DS_STICK_C + "-X", (((float)res.getInteger(R.integer.N3DS_STICK_C_X) / 1000) * maxX));
    sPrefsEditor.putFloat(ButtonType.N3DS_STICK_C + "-Y", (((float)res.getInteger(R.integer.N3DS_STICK_C_Y) / 1000) * maxY));
    sPrefsEditor.putFloat(ButtonType.N3DS_STICK_LEFT + "-X", (((float)res.getInteger(R.integer.N3DS_STICK_MAIN_X) / 1000) * maxX));
    sPrefsEditor.putFloat(ButtonType.N3DS_STICK_LEFT + "-Y", (((float)res.getInteger(R.integer.N3DS_STICK_MAIN_Y) / 1000) * maxY));
    // We want to commit right away, otherwise the overlay could load before this is saved.
    sPrefsEditor.commit();
  }
  public boolean isInEditMode()
  {
    return mIsInEditMode;
  }
}
