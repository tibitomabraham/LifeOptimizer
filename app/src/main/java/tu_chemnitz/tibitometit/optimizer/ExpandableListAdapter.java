package tu_chemnitz.tibitometit.optimizer;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by TibiTom on 28-12-2016.
 */

class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Activity activity;
    private static LayoutInflater inflater=null;
    private Context _context;
    private List<String> _listDataHeader; // header titles
    // Format: Header title, Child title
    private HashMap<String, List<Tasks>> _listDataChild;

    ExpandableListAdapter(Context context, List<String> listDataHeader,
                          HashMap<String, List<Tasks>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
       // inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        String [] headerDetails = headerTitle.split("_");

        TextView tvListHeader = (TextView) convertView.findViewById(R.id.tvListHeader);
        tvListHeader.setTypeface(null, Typeface.BOLD);
        tvListHeader.setText(headerDetails[0]);

        TextView tvListNumber = (TextView) convertView.findViewById(R.id.tvListNumber);
        tvListNumber.setTypeface(null, Typeface.BOLD);

        if (Integer.parseInt(headerDetails[1])>5)
        {
            tvListNumber.setTextColor(Color.rgb(255,0,0));
        }
        else
        {
            tvListNumber.setTextColor(Color.parseColor("#f9f93d"));
        }
        tvListNumber.setText(headerDetails[1]);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final Tasks child = (Tasks) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }

        TextView tvTaskName = (TextView) convertView.findViewById(R.id.task);

        tvTaskName.setText(child.get_name());
        TextView tvTaskDescription= (TextView)convertView.findViewById(R.id.taskDescription); // Task Description
        TextView tvDatetime = (TextView)convertView.findViewById(R.id.datetime); // Datetime
        ImageView ivThumb_image=(ImageView)convertView.findViewById(R.id.list_image); // thumb image

        //tvTask.setText("Task 1");
        tvTaskDescription.setText(child.get_description());
        Date date = child.get_deadline();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = dateFormat.format(date);
        dateFormat.applyPattern("HH:mm");
        String timeString = dateFormat.format(date);

        LinearLayout imageLayout = (LinearLayout) convertView.findViewById(R.id.thumbnail);
        if(child.is_status()) {
            imageLayout.setBackgroundColor(Color.CYAN);
        }
        else{
            imageLayout.setBackgroundColor(Color.RED);
        }
        tvDatetime.setText(dateString+"  "+timeString);
        switch(child.get_category()) {
            case "IU": {
                ivThumb_image.setImageResource(R.drawable.imp_urg);
                break;
            }
            case "I": {
                ivThumb_image.setImageResource(R.drawable.imp);
                break;
            }
            case "U": {
                ivThumb_image.setImageResource(R.drawable.urg);
                break;
            }
            case "N": {
                ivThumb_image.setImageResource(R.drawable.not_imp_urg);
                break;
            }
            default: {
                ivThumb_image.setImageResource(R.drawable.not_imp_urg);
                break;
            }
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
