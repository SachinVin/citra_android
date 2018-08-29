package org.citra.citra_android.model;

public final class TvSettingsItem
{
  private final int mItemId;
  private final int mIconId;
  private final int mLabelId;

  public TvSettingsItem(int itemId, int iconId, int labelId)
  {
    mItemId = itemId;
    mIconId = iconId;
    mLabelId = labelId;
  }

  public int getItemId()
  {
    return mItemId;
  }

  public int getIconId()
  {
    return mIconId;
  }

  public int getLabelId()
  {
    return mLabelId;
  }
}
