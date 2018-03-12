package ucsc.ettendance;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by katelynsuhr on 3/11/18.
 */

public class pAnnouncementAdapter extends ArrayAdapter<AnnouncementModel> {
    private LayoutInflater inflator;
    pAnnouncementAdapter(Context context, ArrayList<AnnouncementModel> users) {
        super(context, 0, users);
        this.inflator = inflator;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        AnnouncementModel user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.p_announcements_list, parent, false);
        }
        // Lookup view for data population
        TextView date = (TextView) convertView.findViewById(R.id.announcementDate);
        TextView name = (TextView) convertView.findViewById(R.id.announcementTitle);
        // Populate the data into the template view using the data object
        date.setText(user.date);
        name.setText(user.announcement);
        // Return the completed view to render on screen
        return convertView;
    }
}
