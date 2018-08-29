package org.citra.citra_android.ui.platform;


import org.citra.citra_android.DolphinApplication;
import org.citra.citra_android.model.GameDatabase;
import org.citra.citra_android.utils.Log;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public final class PlatformGamesPresenter
{
  private final PlatformGamesView mView;

  private Platform mPlatform;

  public PlatformGamesPresenter(PlatformGamesView view)
  {
    mView = view;
  }

  public void onCreate(Platform platform)
  {
    mPlatform = platform;
  }

  public void onCreateView()
  {
    loadGames();
  }

  public void refresh()
  {
    Log.debug("[PlatformGamesPresenter] " + mPlatform + ": Refreshing...");
    loadGames();
  }

  private void loadGames()
  {
    Log.debug("[PlatformGamesPresenter] " + mPlatform + ": Loading games...");

    GameDatabase databaseHelper = DolphinApplication.databaseHelper;

    databaseHelper.getGamesForPlatform(mPlatform)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(games ->
            {
              Log.debug("[PlatformGamesPresenter] " + mPlatform +
                      ": Load finished, swapping cursor...");

              mView.showGames(games);
            });
  }
}
