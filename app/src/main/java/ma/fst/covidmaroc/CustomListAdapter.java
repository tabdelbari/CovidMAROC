package ma.fst.covidmaroc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ma.fst.covidmaroc.model.Path;

public class CustomListAdapter extends BaseAdapter {

    private List<Path> listData;
    private LayoutInflater layoutInflater;
    private Context context;

    public CustomListAdapter(Context context,  List<Path> listData) {
        this.context = context;
        this.listData = listData;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int i) {
        return listData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_item_layout, null);
            holder = new ViewHolder();
            holder.pathName = (TextView) convertView.findViewById(R.id.pathName);
            holder.pathCollision = (TextView) convertView.findViewById(R.id.pathCollision);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Path path = this.listData.get(position);
        holder.pathName.setText(path.getDate());
        holder.pathCollision.setText(path.getCollision() + "");
        convertView.setBackgroundColor(path.getColor()); // white = -1 | yellow = -256 | red = -65536
        return convertView;
    }

    static class ViewHolder {
        TextView pathName;
        TextView pathCollision;
    }
}
