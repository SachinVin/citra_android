package org.citra.citra_android;

import android.app.Application;

import org.citra.citra_android.model.GameDatabase;
import org.citra.citra_android.services.DirectoryInitializationService;
import org.citra.citra_android.utils.PermissionsHandler;

public class DolphinApplication extends Application
{
  public static GameDatabase databaseHelper;

  @Override
  public void onCreate()
  {
    super.onCreate();

    if (PermissionsHandler.hasWriteAccess(getApplicationContext()))
      DirectoryInitializationService.startService(getApplicationContext());

    databaseHelper = new GameDatabase(this);
  }
}
