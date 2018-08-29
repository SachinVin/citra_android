package org.citra.citra_android.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.citra.citra_android.R;

/**
 * A simple class that stores references to views so that the FileAdapter doesn't need to
 * keep calling findViewById(), which is expensive.
 */
public class FileViewHolder extends RecyclerView.ViewHolder
{
  public View itemView;

  public TextView textFileName;
  public ImageView imageType;

  public FileViewHolder(View itemView)
  {
    super(itemView);

    this.itemView = itemView;

    textFileName = itemView.findViewById(R.id.text_file_name);
    imageType = itemView.findViewById(R.id.image_type);
  }
}
