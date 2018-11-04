package org.citra.citra_android.ui.main;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.app.BrowseSupportFragment;
import android.support.v17.leanback.database.CursorMapper;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.CursorObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import org.citra.citra_android.R;
import org.citra.citra_android.activities.EmulationActivity;
import org.citra.citra_android.adapters.GameRowPresenter;
import org.citra.citra_android.adapters.SettingsRowPresenter;
import org.citra.citra_android.model.Game;
import org.citra.citra_android.model.TvSettingsItem;
import org.citra.citra_android.services.DirectoryInitializationService;
import org.citra.citra_android.ui.platform.Platform;
import org.citra.citra_android.ui.settings.SettingsActivity;
import org.citra.citra_android.utils.AddDirectoryHelper;
import org.citra.citra_android.utils.FileBrowserHelper;
import org.citra.citra_android.utils.PermissionsHandler;
import org.citra.citra_android.utils.StartupHandler;
import org.citra.citra_android.viewholders.TvGameViewHolder;

public final class TvMainActivity extends FragmentActivity implements MainView
{
  private MainPresenter mPresenter = new MainPresenter(this);

  private BrowseSupportFragment mBrowseFragment;

  private ArrayObjectAdapter mRowsAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_tv_main);

    setupUI();

    mPresenter.onCreate();

    // Stuff in this block only happens when this activity is newly created (i.e. not a rotation)
    if (savedInstanceState == null)
      StartupHandler.HandleInit(this);
  }

  @Override
  protected void onResume()
  {
    super.onResume();
    mPresenter.addDirIfNeeded(new AddDirectoryHelper(this));
  }

  void setupUI()
  {
    final FragmentManager fragmentManager = getSupportFragmentManager();
    mBrowseFragment = new BrowseSupportFragment();
    fragmentManager
            .beginTransaction()
            .add(R.id.content, mBrowseFragment, "BrowseFragment")
            .commit();

    // Set display parameters for the BrowseFragment
    mBrowseFragment.setHeadersState(BrowseFragment.HEADERS_ENABLED);
    mBrowseFragment.setBrandColor(ContextCompat.getColor(this, R.color.citra_orange_dark));
    buildRowsAdapter();

    mBrowseFragment.setOnItemViewClickedListener(
            (itemViewHolder, item, rowViewHolder, row) ->
            {
              // Special case: user clicked on a settings row item.
              if (item instanceof TvSettingsItem)
              {
                TvSettingsItem settingsItem = (TvSettingsItem) item;
                mPresenter.handleOptionSelection(settingsItem.getItemId());
              }
              else
              {
                TvGameViewHolder holder = (TvGameViewHolder) itemViewHolder;

                // Start the emulation activity and send the path of the clicked ISO to it.
                EmulationActivity.launch(TvMainActivity.this,
                        holder.path,
                        holder.title,
                        holder.screenshotPath,
                        -1,
                        holder.imageScreenshot);
              }
            });
  }

  /**
   * MainView
   */

  @Override
  public void setVersionString(String version)
  {
    mBrowseFragment.setTitle(version);
  }

  @Override
  public void refresh()
  {
    recreate();
  }

  @Override
  public void refreshFragmentScreenshot(int fragmentPosition)
  {
    mRowsAdapter.notifyArrayItemRangeChanged(0, mRowsAdapter.size());
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
    ListRow row = buildGamesRow(platform, games);

    // Add row to the adapter only if it is not empty.
    if (row != null)
    {
      mRowsAdapter.add(row);
    }
  }

  /**
   * Callback from AddDirectoryActivity. Applies any changes necessary to the GameGridActivity.
   *
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
          loadGames();
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

  private void buildRowsAdapter()
  {
    mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());

    if (PermissionsHandler.hasWriteAccess(this))
    {
      loadGames();
    }

    mRowsAdapter.add(buildSettingsRow());

    mBrowseFragment.setAdapter(mRowsAdapter);
  }

  private void loadGames()
  {
    for (Platform platform : Platform.values())
    {
      mPresenter.loadGames(platform);
    }
  }

  private ListRow buildGamesRow(Platform platform, Cursor games)
  {
    // Create an adapter for this row.
    CursorObjectAdapter row = new CursorObjectAdapter(new GameRowPresenter());

    // If cursor is empty, don't return a Row.
    if (!games.moveToFirst())
    {
      return null;
    }

    row.changeCursor(games);
    row.setMapper(new CursorMapper()
    {
      @Override
      protected void bindColumns(Cursor cursor)
      {
        // No-op? Not sure what this does.
      }

      @Override
      protected Object bind(Cursor cursor)
      {
        return Game.fromCursor(cursor);
      }
    });

    String headerName = platform.getHeaderName();

    // Create a header for this row.
    HeaderItem header = new HeaderItem(platform.toInt(), platform.getHeaderName());

    // Create the row, passing it the filled adapter and the header, and give it to the master adapter.
    return new ListRow(header, row);
  }

  private ListRow buildSettingsRow()
  {
    ArrayObjectAdapter rowItems = new ArrayObjectAdapter(new SettingsRowPresenter());

    rowItems.add(new TvSettingsItem(R.id.menu_settings_core,
            R.drawable.ic_settings_core_tv,
            R.string.grid_menu_core_settings));

    rowItems.add(new TvSettingsItem(R.id.button_add_directory,
            R.drawable.ic_add_tv,
            R.string.add_directory_title));

    rowItems.add(new TvSettingsItem(R.id.menu_refresh,
            R.drawable.ic_refresh_tv,
            R.string.grid_menu_refresh));

    // Create a header for this row.
    HeaderItem header =
            new HeaderItem(R.string.preferences_settings, getString(R.string.preferences_settings));

    return new ListRow(header, rowItems);
  }
}
