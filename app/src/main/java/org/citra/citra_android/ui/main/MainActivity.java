package org.citra.citra_android.ui.main;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import org.citra.citra_android.R;
import org.citra.citra_android.model.GameProvider;
import org.citra.citra_android.services.DirectoryInitializationService;
import org.citra.citra_android.ui.platform.Platform;
import org.citra.citra_android.ui.platform.PlatformGamesFragment;
import org.citra.citra_android.ui.platform.PlatformGamesView;
import org.citra.citra_android.ui.settings.SettingsActivity;
import org.citra.citra_android.utils.AddDirectoryHelper;
import org.citra.citra_android.utils.FileBrowserHelper;
import org.citra.citra_android.utils.PermissionsHandler;
import org.citra.citra_android.utils.StartupHandler;

/**
 * The main Activity of the Lollipop style UI. Manages several PlatformGamesFragments, which
 * individually display a grid of available games for each Fragment, in a tabbed layout.
 */
public final class MainActivity extends AppCompatActivity implements MainView
{
  private ViewPager mViewPager;
  private Toolbar mToolbar;
  private TabLayout mTabLayout;
  private int mFrameLayoutId;
  private PlatformGamesFragment mPlatformGamesFragment;
  private FloatingActionButton mFab;

  private MainPresenter mPresenter = new MainPresenter(this);

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    findViews();

    setSupportActionBar(mToolbar);

    mFrameLayoutId = R.id.games_platform_frame;

    // Set up the FAB.
    mFab.setOnClickListener(view -> mPresenter.onFabClick());

    mPresenter.onCreate();

    // Stuff in this block only happens when this activity is newly created (i.e. not a rotation)
    if (savedInstanceState == null)
      StartupHandler.HandleInit(this);

    if (PermissionsHandler.hasWriteAccess(this))
    {
      mPlatformGamesFragment = PlatformGamesFragment.newInstance(Platform.fromInt(0));
      getSupportFragmentManager().beginTransaction().add(mFrameLayoutId, mPlatformGamesFragment)
              .commit();
    }
  }

  @Override
  protected void onResume()
  {
    super.onResume();
    mPresenter.addDirIfNeeded(new AddDirectoryHelper(this));
  }

  // TODO: Replace with a ButterKnife injection.
  private void findViews()
  {
    mToolbar = findViewById(R.id.toolbar_main);
    mFab = findViewById(R.id.button_add_directory);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_game_grid, menu);
    return true;
  }

  /**
   * MainView
   */

  @Override
  public void setVersionString(String version)
  {
    mToolbar.setSubtitle(version);
  }

  @Override
  public void refresh()
  {
    getContentResolver().insert(GameProvider.URI_REFRESH, null);
    refreshFragment();
  }

  @Override
  public void refreshFragmentScreenshot(int fragmentPosition)
  {
    // Invalidate Picasso image so that the new screenshot is animated in.
    PlatformGamesView fragment = getPlatformGamesView();

    if (fragment != null)
    {
      fragment.refreshScreenshotAtPosition(fragmentPosition);
    }
  }

  @Override
  public void launchSettingsActivity(String menuTag)
  {
    SettingsActivity.launch(this, menuTag, "");
  }

  @Override
  public void launchFileListActivity()
  {
    FileBrowserHelper.openDirectoryPicker(this);
  }

  @Override
  public void showGames(Platform platform, Cursor games)
  {
    // no-op. Handled by PlatformGamesFragment.
  }

  /**
   * @param requestCode An int describing whether the Activity that is returning did so successfully.
   * @param resultCode  An int describing what Activity is giving us this callback.
   * @param result      The information the returning Activity is providing us.
   */
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent result)
  {
    switch (requestCode)
    {
      case MainPresenter.REQUEST_ADD_DIRECTORY:
        // If the user picked a file, as opposed to just backing out.
        if (resultCode == MainActivity.RESULT_OK)
        {
          mPresenter.onDirectorySelected(FileBrowserHelper.getSelectedDirectory(result));
        }
        break;

      case MainPresenter.REQUEST_EMULATE_GAME:
        mPresenter.refreshFragmentScreenshot(resultCode);
        break;
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
  {
    switch (requestCode)
    {
      case PermissionsHandler.REQUEST_CODE_WRITE_PERMISSION:
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
          DirectoryInitializationService.startService(this);

          mPlatformGamesFragment = PlatformGamesFragment.newInstance(Platform.fromInt(0));
          getSupportFragmentManager().beginTransaction().add(mFrameLayoutId, mPlatformGamesFragment)
                  .commit();
        }
        else
        {
          Toast.makeText(this, R.string.write_permission_needed, Toast.LENGTH_SHORT)
                  .show();
        }
        break;
      default:
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        break;
    }
  }

  /**
   * Called by the framework whenever any actionbar/toolbar icon is clicked.
   *
   * @param item The icon that was clicked on.
   * @return True if the event was handled, false to bubble it up to the OS.
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    return mPresenter.handleOptionSelection(item.getItemId());
  }

  private void refreshFragment()
  {
    if (mPlatformGamesFragment != null)
    {
      mPlatformGamesFragment.refresh();
    }
  }

  @Nullable
  private PlatformGamesView getPlatformGamesView()
  {
    return (PlatformGamesView) getSupportFragmentManager().findFragmentById(mFrameLayoutId);
  }
}
