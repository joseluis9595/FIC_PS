package es.udc.psi1718.project.view.customviews.pannelslist;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import es.udc.psi1718.project.R;
import es.udc.psi1718.project.storage.database.MySQLiteHelper;

public class PanelCursorAdapter extends CursorAdapter {


	public PanelCursorAdapter(Context context, Cursor cursor) {
		super(context, cursor, 0);
	}


	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return LayoutInflater.from(context).inflate(R.layout.panel_item, parent, false);
	}


	@Override
	public void bindView(View view, Context context, Cursor cursor) {

		// Find fields to populate in inflated template
		TextView tvName = (TextView) view.findViewById(R.id.tv_panelitem_name);

		// TODO remove the background color change
		// Change background color
		// LinearLayout backgroundLayout = (LinearLayout) view.findViewById(R.id.panelitem_background);
		// backgroundLayout.setBackgroundColor(context.getResources().getColor(R.color.colorLightBlue));

		// Extract properties from cursor
		String name = cursor.getString(cursor.getColumnIndexOrThrow(MySQLiteHelper.COL_PANEL_NAME));
		tvName.setText(name);
	}
}
