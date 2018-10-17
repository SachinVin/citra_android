package org.citra.citra_android.ui.main;


import org.citra.citra_android.BuildConfig;
import org.citra.citra_android.DolphinApplication;
import org.citra.citra_android.R;
import org.citra.citra_android.model.GameDatabase;
import org.citra.citra_android.ui.platform.Platform;
import org.citra.citra_android.utils.AddDirectoryHelper;
import org.citra.citra_android.utils.SettingsFile;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public final class MainPresenter
{
  public static final int REQUEST_ADD_DIRECTORY = 1;
  public static final int REQUEST_EMULATE_GAME = 2;

  private final MainView mView;
  private String mDirToAdd;

  public MainPresenter(MainView view)
  {
    mView = view;
  }

  public void onCreate()
  {
    String versionName = BuildConfig.VERSION_NAME;
    mView.setVersionString(versionName);
  }

  public void onFabClick()
  {
    mView.launchFileListActivity();
  }

  public boolean handleOptionSelection(int itemId)
  {
    switch (itemId)
    {
      case R.id.menu_settings_core:
        mView.launchSettingsActivity(SettingsFile.FILE_NAME_CONFIG);
        return true;

      case R.id.menu_refresh:
        GameDatabase databaseHelper = DolphinApplication.databaseHelper;
        databaseHelper.scanLibrary(databaseHelper.getWritableDatabase());
        mView.refresh();
        return true;

      case R.id.button_add_directory:
        mView.launchFileListActivity();
        return true;
    }

    return false;
  }

  public void addDirIfNeeded(AddDirectoryHelper helper)
  {
    if (mDirToAdd != null)
    {
      helper.addDirectory(mDirToAdd, mView::refresh);

      mDirToAdd = null;
    }
  }

  public void onDirectorySelected(String dir)
  {
    mDirToAdd = dir;
  }

  public void refreshFragmentScreenshot(int resultCode)
  {
    mView.refreshFragmentScreenshot(resultCode);
  }


  public void loadGames(final Platform platform)
  {
    GameDatabase databaseHelper = DolphinApplication.databaseHelper;

    databaseHelper.getGamesForPlatform(platform)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(games -> mView.showGames(platform, games));
  }
}
