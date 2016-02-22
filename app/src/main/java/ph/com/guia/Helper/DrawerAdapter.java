package ph.com.guia.Helper;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ph.com.guia.Model.DrawerItem;
import ph.com.guia.R;

public class DrawerAdapter extends ArrayAdapter<DrawerItem> {

    Context mContext;
    int mLayoutResourceId;
    DrawerItem[] mItems;

    public DrawerAdapter(Context context, int resource, DrawerItem[] objects) {
        super(context, resource, objects);

        mContext = context;
        mLayoutResourceId = resource;
        mItems = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View view = inflater.inflate(mLayoutResourceId, parent, false);

        ImageView icon = (ImageView) view.findViewById(R.id.drawer_icon);
        TextView title = (TextView) view.findViewById(R.id.drawer_title);

        icon.setImageResource(mItems[position].icon);
        title.setText(mItems[position].name);

        return view;
    }
}
